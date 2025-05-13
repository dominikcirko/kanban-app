import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { DragDropContext, DropResult } from '@hello-pangea/dnd';
import { Task, Status } from '../types/task';
import { taskApi } from '../services/api';
import { TaskBoard } from './TaskBoard';
import { websocketService } from '../services/websocket';

export const TaskBoardContainer: React.FC = () => {
    const queryClient = useQueryClient();

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

    if (isLoading || !tasksData?.content) {
        return null;
    }

    return (
        <TaskBoard
            tasks={tasksData.content}
            onTaskUpdate={handleTaskUpdate}
            onTaskDelete={handleTaskDelete}
            onTaskCreate={handleTaskCreate}
            onTaskReorder={handleTaskReorder}
        />
    );
}; 