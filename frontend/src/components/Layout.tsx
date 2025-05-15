import React, { useState } from 'react';
import {
    AppBar,
    Box,
    Container,
    Toolbar,
    Typography,
    IconButton,
    useTheme,
    useMediaQuery,
    Button,
    Stack,
} from '@mui/material';
import { Menu as MenuIcon, AccountCircle } from '@mui/icons-material';
import { Sidebar } from './Sidebar';
import LoginDialog from './LoginDialog';
import RegisterDialog from './RegisterDialog';

interface LayoutProps {
    children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

    // State for dialog visibility
    const [loginOpen, setLoginOpen] = useState(false);
    const [registerOpen, setRegisterOpen] = useState(false);

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh' }}>
            <Sidebar />
            <Box sx={{ display: 'flex', flexDirection: 'column', flexGrow: 1, minHeight: '100vh' }}>
                <AppBar 
                    position="sticky" 
                    color="default" 
                    elevation={0}
                    sx={{ 
                        borderBottom: '1px solid',
                        borderColor: 'divider',
                        backgroundColor: 'background.paper',
                    }}
                >
                    <Toolbar>
                        {isMobile && (
                            <IconButton
                                edge="start"
                                color="inherit"
                                aria-label="menu"
                                sx={{ mr: 2 }}
                            >
                                <MenuIcon />
                            </IconButton>
                        )}
                        <Typography 
                            variant="h6" 
                            component="div" 
                            sx={{ 
                                flexGrow: 1,
                                color: 'primary.main',
                                fontWeight: 600,
                            }}
                        >
                            Kanban Board
                        </Typography>
                        <Stack direction="row" spacing={2}>
                            <Button
                                variant="outlined"
                                color="primary"
                                startIcon={<AccountCircle />}
                                onClick={() => setLoginOpen(true)}
                            >
                                Login
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={() => setRegisterOpen(true)}
                            >
                                Register
                            </Button>
                        </Stack>
                    </Toolbar>
                </AppBar>
                {/* Dialogs for login/register */}
                <LoginDialog open={loginOpen} onClose={() => setLoginOpen(false)} />
                <RegisterDialog open={registerOpen} onClose={() => setRegisterOpen(false)} />
                <Box 
                    component="main" 
                    sx={{ 
                        flexGrow: 1,
                        py: 4,
                        backgroundColor: 'background.default',
                        minHeight: 'calc(100vh - 128px)', 
                    }}
                >
                    <Container maxWidth="xl" sx={{ height: '100%' }}>
                        {children}
                    </Container>
                </Box>
                <Box 
                    component="footer" 
                    sx={{ 
                        py: 3,
                        px: 2,
                        mt: 'auto',
                        backgroundColor: 'background.paper',
                        borderTop: '1px solid',
                        borderColor: 'divider',
                    }}
                >
                    <Container maxWidth="xl">
                        <Typography 
                            variant="body2" 
                            color="text.secondary" 
                            align="center"
                        >
                            © {new Date().getFullYear()} Kanban Board. All rights reserved.
                        </Typography>
                    </Container>
                </Box>
            </Box>
        </Box>
    );
}; 