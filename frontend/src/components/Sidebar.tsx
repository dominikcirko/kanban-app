import React, { useState } from 'react';
import {
  Drawer,
  Box,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  ListItemButton,
  IconButton,
  Divider,
  Typography,
  useTheme,
  useMediaQuery,
  Toolbar,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  Assignment as AssignmentIcon,
  Menu as MenuIcon,
  ChevronLeft as ChevronLeftIcon,
} from '@mui/icons-material';

const drawerWidth = 240;

export const Sidebar: React.FC = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [open, setOpen] = useState(!isMobile);
  const [drawerOpen, setDrawerOpen] = useState(false);

  const handleDrawerToggle = () => {
    if (isMobile) {
      setDrawerOpen(!drawerOpen);
    } else {
      setOpen(!open);
    }
  };

  const drawerContent = (
    <Box sx={{ width: open ? drawerWidth : 64, transition: 'width 0.2s' }}>
      <Toolbar sx={{ justifyContent: open ? 'flex-end' : 'center', px: 1 }}>
        <IconButton onClick={handleDrawerToggle} size="small">
          {open ? <ChevronLeftIcon /> : <MenuIcon />}
        </IconButton>
      </Toolbar>
      <Divider />
      <List>
        <ListItem disablePadding>
          <ListItemButton selected>
            <ListItemIcon>
              <DashboardIcon />
            </ListItemIcon>
            {open && <ListItemText primary="Dashboard" />}
          </ListItemButton>
        </ListItem>
        <ListItem disablePadding>
          <ListItemButton>
            <ListItemIcon>
              <AssignmentIcon />
            </ListItemIcon>
            {open && <ListItemText primary="Projects" />}
          </ListItemButton>
        </ListItem>
      </List>
      <Box sx={{ flexGrow: 1 }} />
      <Divider />
      <Box sx={{ p: open ? 2 : 1, textAlign: open ? 'left' : 'center' }}>
        {open && (
          <>
            <Typography variant="body2" color="text.secondary">
              Signed in as
            </Typography>
            <Typography variant="subtitle2">Guest</Typography>
          </>
        )}
        {!open && (
          <Typography variant="caption" color="text.secondary">
            G
          </Typography>
        )}
      </Box>
    </Box>
  );

  if (isMobile) {
    return (
      <>
        <IconButton
          color="inherit"
          aria-label="open drawer"
          edge="start"
          onClick={handleDrawerToggle}
          sx={{ ml: 1, mt: 1, position: 'fixed', zIndex: theme.zIndex.drawer + 1 }}
        >
          <MenuIcon />
        </IconButton>
        <Drawer
          variant="temporary"
          open={drawerOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            '& .MuiDrawer-paper': { width: drawerWidth },
          }}
        >
          {drawerContent}
        </Drawer>
      </>
    );
  }

  return (
    <Drawer
      variant="permanent"
      open={open}
      sx={{
        width: open ? drawerWidth : 64,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: open ? drawerWidth : 64,
          boxSizing: 'border-box',
          transition: 'width 0.2s',
        },
      }}
    >
      {drawerContent}
    </Drawer>
  );
}; 