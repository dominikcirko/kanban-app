import React from 'react';
import { ThemeProvider, CssBaseline } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { theme } from './theme';
import { Layout } from './components/Layout';
import { TaskBoardContainer } from './components/TaskBoardContainer';

const queryClient = new QueryClient();

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Layout>
          <TaskBoardContainer />
        </Layout>
      </ThemeProvider>
    </QueryClientProvider>
  );
};
