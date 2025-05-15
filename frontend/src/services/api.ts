import axios from 'axios';
import { Task, Status, PageResponse, Priority } from '../types/task';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true  // Add this to handle cookies
});

// Attach JWT to all requests if present
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('jwt');
        if (token) {
            config.headers = config.headers || {};
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Add response interceptor to handle token expiration
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 403 && error.response?.data?.message?.includes('expired')) {
            // Only redirect if token is actually expired
            localStorage.removeItem('jwt');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

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
        const token = localStorage.getItem('jwt');
        console.log('JWT about to be sent (PUT):', token);
        const response = await api.put<Task>(`/tasks/${id}`, task, {
            headers: {
                'Authorization': token ? `Bearer ${token}` : '',
                'Content-Type': 'application/json',
            },
        });
        return response.data;
    },

    partialUpdateTask: async (id: number, patch: Partial<Task>) => {
        const token = localStorage.getItem('jwt');
        console.log('--- PATCH REQUEST DEBUG ---');
        console.log('Task ID:', id);
        console.log('Patch payload:', JSON.stringify(patch));
        console.log('Raw JWT from localStorage:', token);
        
        if (!token) {
            console.error('No JWT token found in localStorage');
            throw new Error('Authentication required');
        }

        const response = await api.patch<Task>(`/tasks/${id}`, patch, {
            headers: {
                'Content-Type': 'application/mergepatch+json',
                'Accept': 'application/json'
            }
        });
        
        console.log('PATCH Response:', response.data);
        return response.data;
    },

    deleteTask: async (id: number) => {
        await api.delete(`/tasks/${id}`);
    },
};

export const authApi = {
    login: async (username: string, password: string) => {
        // The backend expects a User object; adjust field names if needed
        const response = await axios.post('http://localhost:8080/auth/login', {
            username,
            password,
        }, {
            headers: { 'Content-Type': 'application/json' },
            // withCredentials: true // Uncomment if backend uses cookies
        });
        return response;
    },
    register: async (username: string, password: string) => {
        const response = await axios.post('http://localhost:8080/auth/register', {
            username,
            password,
        }, {
            headers: { 'Content-Type': 'application/json' },
        });
        return response;
    },
}; 