import React from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    MenuItem,
} from '@mui/material';
import { Task, Status, Priority } from '../types/task';

interface TaskFormProps {
    open: boolean;
    onClose: () => void;
    onSubmit: (task: Partial<Task>) => void;
    initialTask?: Task;
}

export const TaskForm: React.FC<TaskFormProps> = ({
    open,
    onClose,
    onSubmit,
    initialTask,
}) => {
    const [formData, setFormData] = React.useState<Partial<Task>>({
        title: '',
        description: '',
        status: Status.TO_DO,
        priority: Priority.LOW,
        ...initialTask,
    });

    React.useEffect(() => {
        setFormData({
            title: '',
            description: '',
            status: Status.TO_DO,
            priority: Priority.LOW,
            ...initialTask,
        });
    }, [initialTask, open]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
        onClose();
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>
                    {initialTask ? 'Edit Task' : 'Create New Task'}
                </DialogTitle>
                <DialogContent>
                    <TextField
                        name="title"
                        label="Title"
                        value={formData.title}
                        onChange={handleChange}
                        fullWidth
                        margin="normal"
                        required
                    />
                    <TextField
                        name="description"
                        label="Description"
                        value={formData.description}
                        onChange={handleChange}
                        fullWidth
                        margin="normal"
                        multiline
                        rows={4}
                        required
                    />
                    <TextField
                        name="status"
                        label="Status"
                        value={formData.status}
                        onChange={handleChange}
                        select
                        fullWidth
                        margin="normal"
                        required
                    >
                        {Object.values(Status).map((status) => (
                            <MenuItem key={status} value={status}>
                                {status}
                            </MenuItem>
                        ))}
                    </TextField>
                    <TextField
                        name="priority"
                        label="Priority"
                        value={formData.priority}
                        onChange={handleChange}
                        select
                        fullWidth
                        margin="normal"
                        required
                    >
                        {Object.values(Priority).map((priority) => (
                            <MenuItem key={priority} value={priority}>
                                {priority}
                            </MenuItem>
                        ))}
                    </TextField>
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancel</Button>
                    <Button type="submit" variant="contained" color="primary">
                        {initialTask ? 'Update' : 'Create'}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}; 