import React from 'react';
import { Box, Paper, Typography, Chip } from '@mui/material';
import { Droppable } from '@hello-pangea/dnd';
import { Task, Status } from '../types/task';
import { TaskCard } from './TaskCard';

interface TaskColumnProps {
    status: Status;
    tasks: Task[];
    onEdit: (task: Task) => void;
    onDelete: (taskId: number) => void;
}

const statusColors = {
    [Status.TO_DO]: {
        bg: '#f1f5f9',
        text: '#475569',
    },
    [Status.IN_PROGRESS]: {
        bg: '#fef3c7',
        text: '#92400e',
    },
    [Status.DONE]: {
        bg: '#dcfce7',
        text: '#166534',
    },
};

const statusTitles = {
    [Status.TO_DO]: 'To Do',
    [Status.IN_PROGRESS]: 'In Progress',
    [Status.DONE]: 'Done',
};

export const TaskColumn: React.FC<TaskColumnProps> = ({ status, tasks, onEdit, onDelete }) => {
    console.log(`TaskColumn ${status} received tasks:`, tasks);

    return (
        <Paper
            elevation={3}
            sx={{
                p: 2,
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                bgcolor: 'background.default',
            }}
        >
            <Box 
                sx={{ 
                    mb: 2,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                }}
            >
                <Typography 
                    variant="h6" 
                    sx={{ 
                        color: statusColors[status].text,
                        fontWeight: 600,
                    }}
                >
                    {statusTitles[status]}
                </Typography>
                <Chip 
                    label={tasks.length}
                    size="small"
                    sx={{
                        backgroundColor: statusColors[status].text,
                        color: 'white',
                        fontWeight: 500,
                    }}
                />
            </Box>
            <Droppable droppableId={status}>
                {(provided, snapshot) => (
                    <Box
                        ref={provided.innerRef}
                        {...provided.droppableProps}
                        sx={{
                            flexGrow: 1,
                            minHeight: 0,
                            overflowY: 'auto',
                            bgcolor: snapshot.isDraggingOver ? 'action.hover' : 'transparent',
                            transition: 'background-color 0.2s ease',
                            borderRadius: 1,
                            p: 1
                        }}
                    >
                        {tasks.map((task, index) => (
                            <TaskCard
                                key={task.id}
                                task={task}
                                index={index}
                                onEdit={onEdit}
                                onDelete={onDelete}
                            />
                        ))}
                        {provided.placeholder}
                    </Box>
                )}
            </Droppable>
        </Paper>
    );
}; 