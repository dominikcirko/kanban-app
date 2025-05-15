import React from 'react';
import { Box } from '@mui/material';
import { DragDropContext, DropResult } from '@hello-pangea/dnd';
import { Task, Status } from '../types/task';
import { TaskColumn } from './TaskColumn';

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
    console.log('TaskBoard received tasks:', tasks);
    
    const tasksByStatus = tasks.reduce((acc, task) => {
        if (!acc[task.status]) {
            acc[task.status] = [];
        }
        acc[task.status].push(task);
        return acc;
    }, {} as Record<Status, Task[]>);

    Object.values(Status).forEach(status => {
        if (!tasksByStatus[status]) {
            tasksByStatus[status] = [];
        }
    });

    console.log('Tasks grouped by status:', tasksByStatus);

    return (
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
                    height: '100%',
                }}
            >
                {Object.values(Status).map((status) => (
                    <Box 
                        key={status} 
                        sx={{ height: '100%' }}
                    >
                        <TaskColumn
                            status={status}
                            tasks={tasksByStatus[status] || []}
                            onEdit={(task) => onTaskUpdate(task)}
                            onDelete={onTaskDelete}
                        />
                    </Box>
                ))}
            </Box>
        </DragDropContext>
    );
};