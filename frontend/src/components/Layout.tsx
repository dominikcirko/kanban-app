import React from 'react';
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

interface LayoutProps {
    children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

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
                            >
                                Login
                            </Button>
                            <Button
                                variant="contained"
                                color="primary"
                            >
                                Register
                            </Button>
                        </Stack>
                    </Toolbar>
                </AppBar>
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