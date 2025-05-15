import axios from 'axios';
import { Task, Status, PageResponse, Priority } from '../types/task';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export const taskApi = {
    getTasks: async (status?: Status, page = 0, size = 10, sort?: string, priority?: Priority) => {
        const params = new URLSearchParams();
        if (status) params.append('status', status);
        if (priority) params.append('priority', priority);
        params.append('page', page.toString());
        params.append('size', size.toString());
        if (sort) params.append('sort', sort);
        
        const response = await api.get<PageResponse<Task>>('/tasks', { params });
        return response.data;
    },

    getTask: async (id: number) => {
        const response = await api.get<Task>(`/tasks/${id}`);
        return response.data;
    },

    createTask: async (task: Omit<Task, 'id' | 'createdAt' | 'updatedAt'>) => {
        const response = await api.post<Task>('/tasks', task);
        return response.data;
    },

    updateTask: async (id: number, task: Partial<Task>) => {
        const response = await api.put<Task>(`/tasks/${id}`, task);
        return response.data;
    },

    partialUpdateTask: async (id: number, patch: any) => {
        const response = await api.patch<Task>(`/tasks/${id}`, patch, {
            headers: {
                'Content-Type': 'application/mergepatch+json',
            },
        });
        return response.data;
    },

    deleteTask: async (id: number) => {
        await api.delete(`/tasks/${id}`);
    },
}; 