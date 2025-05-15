import React from 'react';
import { Card, CardContent, Typography, IconButton, Box, Chip } from '@mui/material';
import { Draggable } from '@hello-pangea/dnd';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { Task, Priority } from '../types/task';

interface TaskCardProps {
    task: Task;
    index: number;
    onEdit: (task: Task) => void;
    onDelete: (taskId: number) => void;
}

const priorityColors = {
    [Priority.LOW]: 'success',
    [Priority.MED]: 'warning',
    [Priority.HIGH]: 'error',
} as const;

export const TaskCard: React.FC<TaskCardProps> = ({
    task,
    index,
    onEdit,
    onDelete,
}) => {
    console.log('TaskCard rendering task:', task);

    return (
        <Draggable draggableId={task.id.toString()} index={index}>
            {(provided, snapshot) => (
                <Card
                    ref={provided.innerRef}
                    {...provided.draggableProps}
                    {...provided.dragHandleProps}
                    sx={{
                        mb: 2,
                        cursor: 'grab',
                        bgcolor: snapshot.isDragging ? 'action.selected' : 'background.paper',
                        '&:active': {
                            cursor: 'grabbing',
                        },
                    }}
                >
                    <CardContent>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                                {task.title}
                            </Typography>
                            <Box>
                                <IconButton size="small" onClick={() => onEdit(task)}>
                                    <EditIcon />
                                </IconButton>
                                <IconButton size="small" onClick={() => onDelete(task.id)}>
                                    <DeleteIcon />
                                </IconButton>
                            </Box>
                        </Box>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                            {task.description}
                        </Typography>
                        <Chip 
                            label={task.priority} 
                            size="small"
                            color={priorityColors[task.priority]}
                        />
                    </CardContent>
                </Card>
            )}
        </Draggable>
    );
}; 