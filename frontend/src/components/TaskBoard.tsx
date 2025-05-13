import React, { useState } from 'react';
import {
    Box,
    Button,
    Typography,
    TextField,
    InputAdornment,
    IconButton,
    Paper,
    Stack,
    useTheme,
    Grid,
} from '@mui/material';
import {
    Search as SearchIcon,
    Add as AddIcon,
    FilterList as FilterIcon,
} from '@mui/icons-material';
import { DragDropContext, DropResult } from '@hello-pangea/dnd';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Task, Status } from '../types/task';
import { taskApi } from '../services/api';
import { TaskColumn } from './TaskColumn';
import { TaskForm } from './TaskForm';
import { websocketService } from '../services/websocket';

interface TaskBoardProps {
    tasks: Task[];
    onTaskUpdate: (task: Task) => void;
    onTaskDelete: (taskId: number) => void;
    onTaskCreate: (task: Partial<Task>) => void;
    onTaskReorder: (result: DropResult) => void;
}

export const TaskBoard: React.FC<TaskBoardProps> = ({
    tasks,
    onTaskUpdate,
    onTaskDelete,
    onTaskCreate,
    onTaskReorder,
}) => {
    const [isFormOpen, setIsFormOpen] = useState(false);
    const [selectedTask, setSelectedTask] = useState<Task | undefined>();
    const [searchQuery, setSearchQuery] = useState('');
    const queryClient = useQueryClient();
    const theme = useTheme();

    const { data: tasksData, isLoading } = useQuery({
        queryKey: ['tasks'],
        queryFn: () => taskApi.getTasks(),
    });

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

    const deleteTaskMutation = useMutation({
        mutationFn: taskApi.deleteTask,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['tasks'] });
        },
    });

    React.useEffect(() => {
        websocketService.connect();
        const unsubscribe = websocketService.subscribe((notification) => {
            if (notification.type === 'CREATE' || notification.type === 'UPDATE' || notification.type === 'DELETE') {
                queryClient.invalidateQueries({ queryKey: ['tasks'] });
            }
        });

        return () => {
            unsubscribe();
            websocketService.disconnect();
        };
    }, [queryClient]);

    const handleDragEnd = (result: DropResult) => {
        if (!result.destination) return;

        const taskId = parseInt(result.draggableId);
        const newStatus = result.destination.droppableId as Status;

        updateTaskMutation.mutate({
            id: taskId,
            task: { status: newStatus },
        });
    };

    const handleCreateTask = (task: Partial<Task>) => {
        createTaskMutation.mutate(task as Omit<Task, 'id' | 'createdAt' | 'updatedAt'>);
    };

    const handleUpdateTask = (task: Partial<Task>) => {
        if (selectedTask) {
            updateTaskMutation.mutate({
                id: selectedTask.id,
                task,
            });
        }
    };

    const handleDeleteTask = (taskId: number) => {
        deleteTaskMutation.mutate(taskId);
    };

    const filteredTasks = searchQuery
        ? tasks.filter(task =>
            task.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
            task.description.toLowerCase().includes(searchQuery.toLowerCase())
        )
        : tasks;

    const tasksByStatus = {
        [Status.TO_DO]: filteredTasks.filter(task => task.status === Status.TO_DO),
        [Status.IN_PROGRESS]: filteredTasks.filter(task => task.status === Status.IN_PROGRESS),
        [Status.DONE]: filteredTasks.filter(task => task.status === Status.DONE),
    };

    if (isLoading) {
        return <Typography>Loading...</Typography>;
    }

    return (
        <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Paper 
                elevation={0}
                sx={{ 
                    p: 2, 
                    mb: 2,
                    backgroundColor: 'transparent',
                }}
            >
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                    <Typography variant="h5" sx={{ fontWeight: 600, color: '#1e293b' }}>
                        Task Board
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 2 }}>
                        <TextField
                            size="small"
                            placeholder="Search tasks..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon sx={{ color: '#64748b' }} />
                                    </InputAdornment>
                                ),
                            }}
                            sx={{
                                width: 250,
                                '& .MuiOutlinedInput-root': {
                                    backgroundColor: 'white',
                                    '& fieldset': {
                                        borderColor: '#e2e8f0',
                                    },
                                    '&:hover fieldset': {
                                        borderColor: '#94a3b8',
                                    },
                                },
                            }}
                        />
                        <IconButton 
                            sx={{ 
                                backgroundColor: 'white',
                                border: '1px solid #e2e8f0',
                                '&:hover': {
                                    backgroundColor: '#f8fafc',
                                },
                            }}
                        >
                            <FilterIcon sx={{ color: '#64748b' }} />
                        </IconButton>
                        <Button
                            variant="contained"
                            color="primary"
                            startIcon={<AddIcon />}
                            sx={{
                                textTransform: 'none',
                                fontWeight: 500,
                                borderRadius: 2,
                                boxShadow: 'none',
                            }}
                            onClick={() => {
                                setSelectedTask(undefined);
                                setIsFormOpen(true);
                            }}
                        >
                            Add Task
                        </Button>
                    </Box>
                </Box>
            </Paper>

            <DragDropContext onDragEnd={onTaskReorder}>
                <Box 
                    sx={{ 
                        display: 'grid',
                        gridTemplateColumns: {
                            xs: '1fr',
                            md: 'repeat(3, 1fr)',
                        },
                        gap: 2,
                        flexGrow: 1,
                        minHeight: 0,
                    }}
                >
                    {Object.values(Status).map((status) => (
                        <Box 
                            key={status} 
                            sx={{ height: '100%' }}
                        >
                            <TaskColumn
                                status={status}
                                tasks={tasksByStatus[status]}
                                onEdit={(task) => {
                                    setSelectedTask(task);
                                    setIsFormOpen(true);
                                }}
                                onDelete={onTaskDelete}
                            />
                        </Box>
                    ))}
                </Box>
            </DragDropContext>

            <TaskForm
                open={isFormOpen}
                onClose={() => {
                    setIsFormOpen(false);
                    setSelectedTask(undefined);
                }}
                onSubmit={selectedTask ? handleUpdateTask : onTaskCreate}
                initialTask={selectedTask}
            />
        </Box>
    );
}; 