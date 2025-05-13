export enum Status {
    TO_DO = 'TO_DO',
    IN_PROGRESS = 'IN_PROGRESS',
    DONE = 'DONE'
}

export enum Priority {
    LOW = 'LOW',
    MED = 'MED',
    HIGH = 'HIGH'
}

export interface Task {
    id: number;
    title: string;
    description: string;
    status: Status;
    priority: Priority;
    version?: number;
}

export interface TaskNotification {
    type: 'CREATE' | 'UPDATE' | 'DELETE';
    task?: Task;
    taskId?: number;
    timestamp: string;
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
} 