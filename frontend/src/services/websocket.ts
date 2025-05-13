import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { TaskNotification } from '../types/task';

class WebSocketService {
    private client: Client | null = null;
    private subscribers: ((notification: TaskNotification) => void)[] = [];

    connect() {
        this.client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
            onConnect: () => {
                console.log('Connected to WebSocket');
                this.client?.subscribe('/topic/tasks', (message) => {
                    const notification: TaskNotification = JSON.parse(message.body);
                    this.subscribers.forEach(callback => callback(notification));
                });
            },
            onDisconnect: () => {
                console.log('Disconnected from WebSocket');
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame);
            },
        });

        this.client.activate();
    }

    disconnect() {
        if (this.client) {
            this.client.deactivate();
            this.client = null;
        }
    }

    subscribe(callback: (notification: TaskNotification) => void) {
        this.subscribers.push(callback);
        return () => {
            this.subscribers = this.subscribers.filter(cb => cb !== callback);
        };
    }
}

export const websocketService = new WebSocketService(); 