import React, { useState } from 'react';
import { ThemeProvider, CssBaseline, Card, CardContent, Typography, Button, Stack } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { theme } from './theme';
import { Layout } from './components/Layout';
import { TaskBoardContainer } from './components/TaskBoardContainer';
import LoginDialog from './components/LoginDialog';
import RegisterDialog from './components/RegisterDialog';

const queryClient = new QueryClient();

const AuthLanding: React.FC = () => {
  const [loginOpen, setLoginOpen] = useState(false);
  const [registerOpen, setRegisterOpen] = useState(false);

  // When registration is successful, close register and open login
  const handleRegistered = () => {
    setRegisterOpen(false);
    setLoginOpen(true);
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', background: 'linear-gradient(135deg, #e3f2fd 0%, #90caf9 100%)' }}>
      <Card sx={{ minWidth: 350, maxWidth: 400, p: 3, boxShadow: 6 }}>
        <CardContent>
          <Typography variant="h4" align="center" gutterBottom sx={{ fontWeight: 700, color: 'primary.main' }}>
            Welcome to Kanban Board
          </Typography>
          <Typography variant="body1" align="center" color="text.secondary" sx={{ mb: 3 }}>
            Please login or register to continue.
          </Typography>
          <Stack direction="row" spacing={2} justifyContent="center">
            <Button variant="contained" color="primary" onClick={() => setLoginOpen(true)}>
              Login
            </Button>
            <Button variant="outlined" color="primary" onClick={() => setRegisterOpen(true)}>
              Register
            </Button>
          </Stack>
        </CardContent>
      </Card>
      <LoginDialog open={loginOpen} onClose={() => setLoginOpen(false)} />
      <RegisterDialog open={registerOpen} onClose={() => setRegisterOpen(false)} onRegistered={handleRegistered} />
    </div>
  );
};

export const App: React.FC = () => {
  const isAuthenticated = !!localStorage.getItem('jwt');
  return isAuthenticated ? (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <Layout>
          <TaskBoardContainer />
        </Layout>
      </ThemeProvider>
    </QueryClientProvider>
  ) : (
    <AuthLanding />
  );
};
