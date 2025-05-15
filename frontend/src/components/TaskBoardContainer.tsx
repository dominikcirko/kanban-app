import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Box, Stack, MenuItem, Select, FormControl, InputLabel, Pagination, Typography, Paper } from '@mui/material';
import { DragDropContext, DropResult } from '@hello-pangea/dnd';
import { Task, Status, Priority, PageResponse } from '../types/task';
import { taskApi } from '../services/api';
import { TaskBoard } from './TaskBoard';
import { websocketService } from '../services/websocket';

export const TaskBoardContainer: React.FC = () => {
    const queryClient = useQueryClient();

    // Filtering, sorting, and pagination state
    const [statusFilter, setStatusFilter] = useState<string>('');
    const [priorityFilter, setPriorityFilter] = useState<string>('');
    const [sort, setSort] = useState<string>('id,asc');
    const [page, setPage] = useState<number>(1);
    const [size, setSize] = useState<number>(10);

    const { data: tasksData, isLoading } = useQuery<PageResponse<Task>>({
        queryKey: ['tasks', statusFilter, priorityFilter, sort, page, size],
        queryFn: () => taskApi.getTasks(
            statusFilter as Status | undefined,
            page - 1,
            size,
            sort,
            priorityFilter as Priority | undefined // <-- make sure this is passed!
        ),
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

    const partialUpdateTaskMutation = useMutation({
        mutationFn: ({ id, patch }: { id: number; patch: Partial<Task> }) =>
            taskApi.partialUpdateTask(id, patch),
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

    const handleTaskUpdate = (task: Task) => {
        updateTaskMutation.mutate({
            id: task.id,
            task,
        });
    };

    const handleTaskDelete = (taskId: number) => {
        deleteTaskMutation.mutate(taskId);
    };

    const handleTaskCreate = (task: Partial<Task>) => {
        createTaskMutation.mutate(task as Omit<Task, 'id' | 'createdAt' | 'updatedAt'>);
    };

    const handleTaskReorder = (result: DropResult) => {
        if (!result.destination) return;

        const taskId = parseInt(result.draggableId);
        const newStatus = result.destination.droppableId as Status;

        partialUpdateTaskMutation.mutate({
            id: taskId,
            patch: { status: newStatus },
        });
    };

    // Toolbar for filtering, sorting, and pagination
    const Toolbar = (
        <Paper elevation={0} sx={{ p: 2, mb: 2, backgroundColor: 'transparent' }}>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems={{ xs: 'stretch', sm: 'center' }} justifyContent="space-between">
                <Stack direction="row" spacing={2} alignItems="center">
                    <FormControl size="small" variant="outlined" sx={{ minWidth: 120 }}>
                        <InputLabel>Status</InputLabel>
                        <Select
                            value={statusFilter}
                            label="Status"
                            onChange={(e) => { setStatusFilter(e.target.value); setPage(1); }}
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
                            onChange={(e) => { setPriorityFilter(e.target.value); setPage(1); }}
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
                            onChange={(e) => { setSort(e.target.value); setPage(1); }}
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

    if (isLoading || !tasksData?.content) {
        return <Typography>Loading...</Typography>;
    }

    return (
        <Box>
            {Toolbar}
            <TaskBoard
                tasks={tasksData.content}
                onTaskUpdate={handleTaskUpdate}
                onTaskDelete={handleTaskDelete}
                onTaskCreate={handleTaskCreate}
                onTaskReorder={handleTaskReorder}
            />
        </Box>
    );
}; 