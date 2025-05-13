import React from 'react';
import { Paper, Typography, Box, IconButton, Stack, Chip } from '@mui/material';
import { Draggable } from '@hello-pangea/dnd';
import { Task, Status, Priority } from '../types/task';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

interface TaskCardProps {
    task: Task;
    index: number;
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

const priorityColors = {
    [Priority.LOW]: 'default',
    [Priority.MED]: 'warning',
    [Priority.HIGH]: 'error',
};

export const TaskCard: React.FC<TaskCardProps> = ({ task, index, onEdit, onDelete }) => {
    return (
        <Draggable draggableId={task.id.toString()} index={index}>
            {(provided) => (
                <Paper
                    ref={provided.innerRef}
                    {...provided.draggableProps}
                    {...provided.dragHandleProps}
                    elevation={0}
                    sx={{
                        p: 2,
                        mb: 2,
                        backgroundColor: 'white',
                        border: '1px solid',
                        borderColor: '#e2e8f0',
                        '&:hover': {
                            borderColor: '#94a3b8',
                            boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
                        },
                    }}
                >
                    <Box sx={{ mb: 2 }}>
                        <Typography 
                            variant="h6" 
                            sx={{ 
                                fontSize: '1rem',
                                fontWeight: 600,
                                color: '#1e293b',
                                mb: 1,
                            }}
                        >
                            {task.title}
                        </Typography>
                        <Typography 
                            variant="body2" 
                            sx={{ 
                                color: '#64748b',
                                display: '-webkit-box',
                                WebkitLineClamp: 2,
                                WebkitBoxOrient: 'vertical',
                                overflow: 'hidden',
                            }}
                        >
                            {task.description}
                        </Typography>
                    </Box>
                    <Stack direction="row" spacing={1} sx={{ mb: 2 }}>
                        <Chip
                            label={task.status.replace('_', ' ')}
                            size="small"
                            sx={{
                                backgroundColor: statusColors[task.status].bg,
                                color: statusColors[task.status].text,
                                fontWeight: 500,
                            }}
                        />
                        <Chip
                            label={task.priority.charAt(0) + task.priority.slice(1).toLowerCase() + ' Priority'}
                            size="small"
                            color={priorityColors[task.priority] as any}
                        />
                    </Stack>
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
                        <IconButton
                            size="small"
                            onClick={() => onEdit(task)}
                            sx={{ 
                                color: '#64748b',
                                '&:hover': {
                                    color: '#475569',
                                    backgroundColor: '#f1f5f9',
                                },
                            }}
                        >
                            <EditIcon fontSize="small" />
                        </IconButton>
                        <IconButton
                            size="small"
                            onClick={() => onDelete(task.id)}
                            sx={{ 
                                color: '#64748b',
                                '&:hover': {
                                    color: '#ef4444',
                                    backgroundColor: '#fee2e2',
                                },
                            }}
                        >
                            <DeleteIcon fontSize="small" />
                        </IconButton>
                    </Box>
                </Paper>
            )}
        </Draggable>
    );
}; 