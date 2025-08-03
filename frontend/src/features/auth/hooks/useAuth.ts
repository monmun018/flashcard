import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '../../../shared/store/auth';
import { authService } from '../services/authService';
import type { LoginRequest, RegisterRequest } from '../../../shared/types';

export const useAuth = () => {
  const { user, isAuthenticated, isLoading, login, logout, setLoading } = useAuthStore();
  const queryClient = useQueryClient();

  const loginMutation = useMutation({
    mutationFn: authService.login,
    onMutate: () => {
      setLoading(true);
    },
    onSuccess: (data) => {
      login(data.token, data.user);
      queryClient.invalidateQueries({ queryKey: ['user'] });
    },
    onError: (error) => {
      console.error('Login failed:', error);
      setLoading(false);
    },
  });

  const registerMutation = useMutation({
    mutationFn: authService.register,
    onMutate: () => {
      setLoading(true);
    },
    onSuccess: () => {
      setLoading(false);
    },
    onError: (error) => {
      console.error('Registration failed:', error);
      setLoading(false);
    },
  });

  const handleLogin = async (credentials: LoginRequest) => {
    return loginMutation.mutateAsync(credentials);
  };

  const handleRegister = async (userData: RegisterRequest) => {
    return registerMutation.mutateAsync(userData);
  };

  const handleLogout = () => {
    logout();
    queryClient.clear();
  };

  return {
    user,
    isAuthenticated,
    isLoading: isLoading || loginMutation.isPending || registerMutation.isPending,
    login: handleLogin,
    register: handleRegister,
    logout: handleLogout,
    loginError: loginMutation.error,
    registerError: registerMutation.error,
  };
};