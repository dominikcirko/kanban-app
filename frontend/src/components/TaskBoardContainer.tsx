import React, { useState, useRef, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Box, Stack, MenuItem, Select, FormControl, InputLabel, Pagination, Typography, Paper, Button } from '@mui/material';
import { DragDropContext, DropResult } from '@hello-pangea/dnd';
import { Task, Status, Priority, PageResponse, TaskNotification } from '../types/task';
import { taskApi } from '../services/api';
import { TaskBoard } from './TaskBoard';
import { websocketService } from '../services/websocket';
import { TaskDialog } from './TaskDialog';
import AddIcon from '@mui/icons-material/Add';

export const TaskBoardContainer: React.FC = () => {
    const queryClient = useQueryClient();
    const isUpdatingRef = useRef<number | null>(null);
    const pendingDragUpdateRef = useRef<{ taskId: number, newStatus: Status } | null>(null);

    // Filtering, sorting, and pagination state
    const [statusFilter, setStatusFilter] = useState<string>('');
    const [priorityFilter, setPriorityFilter] = useState<string>('');
    const [sort, setSort] = useState<string>('id,asc');
    const [page, setPage] = useState<number>(1);
    const [size, setSize] = useState<number>(10);

    // Local tasks state for optimistic updates
    const [localTasks, setLocalTasks] = useState<Task[]>([]);
    const [isDragging, setIsDragging] = useState(false);

    const [dialogOpen, setDialogOpen] = React.useState(false);
    const [editingTask, setEditingTask] = React.useState<Task | undefined>();

    const { data: tasksData, isLoading, error, refetch } = useQuery<PageResponse<Task>>({
        queryKey: ['tasks', statusFilter, priorityFilter, sort, page, size],
        queryFn: async () => {
            console.log('Fetching tasks with params:', {
                statusFilter,
                priorityFilter,
                sort,
                page: page - 1,
                size
            });
            // When no filter is selected, we want ALL tasks
            const result = await taskApi.getTasks(
                statusFilter === '' ? undefined : (statusFilter as Status),
                page - 1,
                size,
                sort,
                priorityFilter === '' ? undefined : (priorityFilter as Priority)
            );
            console.log('Fetched tasks:', result);
            return result;
        },
        // Ensure we always get fresh data
        refetchOnMount: true,
        refetchOnWindowFocus: true,
    });

    // Update local tasks when data changes
    useEffect(() => {
        if (tasksData?.content && !isDragging) {
            console.log('Updating local tasks from server data');
            setLocalTasks(tasksData.content);
        }
    }, [tasksData, isDragging]);

    // Add debug logging for data state
    useEffect(() => {
        console.log('Current tasks data:', tasksData);
        console.log('Loading state:', isLoading);
        console.log('Error state:', error);
        if (tasksData) {
            console.log('Number of tasks:', tasksData.content.length);
            console.log('Total elements:', tasksData.totalElements);
            console.log('Current page:', page);
            console.log('Page size:', size);
        }
    }, [tasksData, isLoading, error, page, size]);

    const createTaskMutation = useMutation({
        mutationFn: taskApi.createTask,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['tasks'] });
        },
    });

    const updateTaskMutation = useMutation({
        mutationFn: ({ id, task }: { id: number; task: Partial<Task> }) =>
            taskApi.updateTask(id, task),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['tasks'] });
        },
    });

    const partialUpdateTaskMutation = useMutation({
        mutationFn: ({ id, patch }: { id: number; patch: Partial<Task> }) =>
            taskApi.partialUpdateTask(id, patch),
        onSuccess: (updatedTask) => {
            // Compare with our pending drag - if it matches, don't refetch
            if (pendingDragUpdateRef.current && 
                pendingDragUpdateRef.current.taskId === updatedTask.id &&
                pendingDragUpdateRef.current.newStatus === updatedTask.status) {
                console.log('Drag update confirmed by server, keeping optimistic UI update');
                pendingDragUpdateRef.current = null;
                setIsDragging(false);
            } else {
                // For other updates, force a refetch
                queryClient.invalidateQueries({ 
                    queryKey: ['tasks', statusFilter, priorityFilter, sort, page, size]
                });
            }
        }
    });

    const deleteTaskMutation = useMutation({
        mutationFn: taskApi.deleteTask,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['tasks'] });
        },
    });

    // Handle websocket updates
    useEffect(() => {
        websocketService.connect();
        const unsubscribe = websocketService.subscribe((notification) => {
            console.log('Websocket notification received:', notification);
            
            // Type guard to ensure notification.task is a valid Task
            if (!notification.task || typeof notification.task.id !== 'number') {
                console.warn('Received invalid task notification:', notification);
                return;
            }
            
            // If a websocket update comes in for a task we're currently dragging, ignore it
            if (pendingDragUpdateRef.current && pendingDragUpdateRef.current.taskId === notification.task?.id) {
                console.log('Ignoring websocket update for task being dragged:', notification.task.id);
                return;
            }
            
            // Otherwise, update our local state to match the notification
            if (notification.type === 'UPDATE') {
                // Ensure we have a valid Task object from the notification
                const updatedTask = notification.task as Task;
                setLocalTasks(prevTasks => 
                    prevTasks.map(task => 
                        task.id === updatedTask.id ? updatedTask : task
                    )
                );
            } else if (notification.type === 'CREATE' && notification.task) {
                // Handle CREATE by adding the new task to our local state
                setLocalTasks(prevTasks => [...prevTasks, notification.task as Task]);
            } else if (notification.type === 'DELETE' && notification.task) {
                // Handle DELETE by removing the task from our local state
                setLocalTasks(prevTasks => prevTasks.filter(task => task.id !== notification.task?.id));
            } else {
                // For other notification types, just refetch
                queryClient.invalidateQueries({ queryKey: ['tasks'] });
            }
        });

        return () => {
            unsubscribe();
            websocketService.disconnect();
        };
    }, [queryClient]);

    const handleDragStart = () => {
        setIsDragging(true);
    };

    const handleTaskReorder = (result: DropResult) => {
        console.log('DRAG END EVENT:', {
            draggableId: result.draggableId,
            source: result.source,
            destination: result.destination,
            type: result.type,
            reason: result.reason
        });

        // If dropped outside a droppable area
        if (!result.destination) {
            setIsDragging(false);
            return;
        }
        
        const taskId = parseInt(result.draggableId);
        const newStatus = result.destination.droppableId as Status;
        
        console.log('UPDATING TASK:', {
            taskId,
            newStatus,
            currentTasks: localTasks
        });

        // Optimistically update the UI first
        setLocalTasks(prevTasks => 
            prevTasks.map(task => 
                task.id === taskId ? { ...task, status: newStatus } : task
            )
        );
        
        // Save the current drag operation so we can recognize it when the server responds
        pendingDragUpdateRef.current = { taskId, newStatus };

        // Update server
        partialUpdateTaskMutation.mutate({
            id: taskId,
            patch: { status: newStatus }
        });
    };

    const handleTaskUpdate = (task: Task) => {
        setEditingTask(task);
        setDialogOpen(true);
    };

    const handleTaskDelete = (taskId: number) => {
        // Optimistic delete
        setLocalTasks(prevTasks => 
            prevTasks.filter(task => task.id !== taskId)
        );
        
        deleteTaskMutation.mutate(taskId);
    };

    const handleTaskCreate = () => {
        setEditingTask(undefined);
        setDialogOpen(true);
    };

    const handleDialogSave = (task: Partial<Task>) => {
        if (editingTask) {
            // Update existing task
            updateTaskMutation.mutate({
                id: editingTask.id,
                task: { ...editingTask, ...task }
            });
        } else {
            // Create new task
            createTaskMutation.mutate(task as Omit<Task, 'id' | 'createdAt' | 'updatedAt'>);
        }
    };

    // Toolbar for filtering, sorting, and pagination
    const Toolbar = (
        <Paper elevation={0} sx={{ p: 2, mb: 2, backgroundColor: 'transparent' }}>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between">
                <Stack direction="row" spacing={2} alignItems="center">
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={handleTaskCreate}
                        sx={{ mr: 2 }}
                    >
                        New Task
                    </Button>
                    <FormControl size="small" variant="outlined" sx={{ minWidth: 120 }}>
                        <InputLabel>Status</InputLabel>
                        <Select
                            value={statusFilter}
                            label="Status"
                            onChange={(e) => { 
                                setStatusFilter(e.target.value);
                                setPage(1);
                            }}
                        >
                            <MenuItem value="">All</MenuItem>
                            {Object.values(Status).map((status) => (
                                <MenuItem key={status} value={status}>{status.replace('_', ' ')}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl size="small" variant="outlined" sx={{ minWidth: 120 }}>
                        <InputLabel>Priority</InputLabel>
                        <Select
                            value={priorityFilter}
                            label="Priority"
                            onChange={(e) => { 
                                setPriorityFilter(e.target.value);
                                setPage(1);  // Reset to first page when filter changes
                            }}
                        >
                            <MenuItem value="">All</MenuItem>
                            {Object.values(Priority).map((priority) => (
                                <MenuItem key={priority} value={priority}>{priority}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl size="small" sx={{ minWidth: 140 }}>
                        <InputLabel>Sort By</InputLabel>
                        <Select
                            value={sort}
                            label="Sort By"
                            onChange={(e) => { 
                                setSort(e.target.value);
                                setPage(1);  // Reset to first page when sort changes
                            }}
                        >
                            <MenuItem value="id,asc">ID (Asc)</MenuItem>
                            <MenuItem value="id,desc">ID (Desc)</MenuItem>
                            <MenuItem value="title,asc">Title (A-Z)</MenuItem>
                            <MenuItem value="title,desc">Title (Z-A)</MenuItem>
                            <MenuItem value="status,asc">Status (Asc)</MenuItem>
                            <MenuItem value="status,desc">Status (Desc)</MenuItem>
                            <MenuItem value="priority,asc">Priority (Asc)</MenuItem>
                            <MenuItem value="priority,desc">Priority (Desc)</MenuItem>
                        </Select>
                    </FormControl>
                </Stack>
                <Box>
                    {tasksData && tasksData.totalPages > 1 && (
                        <Pagination
                            count={tasksData.totalPages}
                            page={page}
                            onChange={(_, value) => setPage(value)}
                            color="primary"
                            shape="rounded"
                        />
                    )}
                </Box>
            </Stack>
        </Paper>
    );

    if (isLoading && localTasks.length === 0) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Typography>Loading tasks...</Typography>
            </Box>
        );
    }

    if (error) {
        console.error('Error loading tasks:', error);
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Typography color="error">Error loading tasks. Please try again.</Typography>
            </Box>
        );
    }

    console.log('Rendering with tasks:', localTasks);

    return (
        <Box>
            {Toolbar}
            <TaskBoard
                tasks={localTasks}
                onTaskUpdate={handleTaskUpdate}
                onTaskDelete={handleTaskDelete}
                onTaskCreate={handleTaskCreate}
                onTaskReorder={handleTaskReorder}
            />
            <TaskDialog
                open={dialogOpen}
                onClose={() => setDialogOpen(false)}
                onSave={handleDialogSave}
                task={editingTask}
                isNew={!editingTask}
            />
        </Box>
    );
};