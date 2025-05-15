import React from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Box
} from '@mui/material';
import { Task, Status, Priority } from '../types/task';

interface TaskDialogProps {
    open: boolean;
    onClose: () => void;
    onSave: (task: Partial<Task>) => void;
    task?: Task;
    isNew?: boolean;
}

export const TaskDialog: React.FC<TaskDialogProps> = ({
    open,
    onClose,
    onSave,
    task,
    isNew = false
}) => {
    const [formData, setFormData] = React.useState<Partial<Task>>({
        title: '',
        description: '',
        status: Status.TO_DO,
        priority: Priority.MED,
        ...task
    });

    React.useEffect(() => {
        if (task) {
            setFormData(task);
        } else {
            setFormData({
                title: '',
                description: '',
                status: Status.TO_DO,
                priority: Priority.MED
            });
        }
    }, [task]);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSave(formData);
        onClose();
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>{isNew ? 'Create New Task' : 'Edit Task'}</DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 1 }}>
                        <TextField
                            label="Title"
                            value={formData.title}
                            onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                            required
                            fullWidth
                        />
                        <TextField
                            label="Description"
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                            multiline
                            rows={3}
                            fullWidth
                        />
                        <FormControl fullWidth>
                            <InputLabel>Status</InputLabel>
                            <Select
                                value={formData.status}
                                label="Status"
                                onChange={(e) => setFormData({ ...formData, status: e.target.value as Status })}
                            >
                                {Object.values(Status).map((status) => (
                                    <MenuItem key={status} value={status}>
                                        {status.replace('_', ' ')}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <FormControl fullWidth>
                            <InputLabel>Priority</InputLabel>
                            <Select
                                value={formData.priority}
                                label="Priority"
                                onChange={(e) => setFormData({ ...formData, priority: e.target.value as Priority })}
                            >
                                {Object.values(Priority).map((priority) => (
                                    <MenuItem key={priority} value={priority}>
                                        {priority}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancel</Button>
                    <Button type="submit" variant="contained" color="primary">
                        {isNew ? 'Create' : 'Save'}
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}; 