import { apiClient } from '../../../shared/api/client';
import type { LoginRequest, RegisterRequest, LoginResponse, User } from '../../../shared/types';

export const authService = {
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    try {
      const response = await apiClient.post<LoginResponse>('/auth/login', credentials);
      
      if (!response.success || !response.data) {
        throw new Error(response.error || 'Login failed');
      }
      
      return response.data;
    } catch (error: any) {
      // Handle axios error response
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error(error.message || 'Login failed');
    }
  },

  async register(userData: RegisterRequest): Promise<User> {
    try {
      const response = await apiClient.post<User>('/auth/register', userData);
      
      if (!response.success || !response.data) {
        throw new Error(response.error || 'Registration failed');
      }
      
      return response.data;
    } catch (error: any) {
      // Handle axios error response
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error(error.message || 'Registration failed');
    }
  },

  async getCurrentUser(): Promise<User> {
    const response = await apiClient.get<User>('/auth/me');
    
    if (!response.success || !response.data) {
      throw new Error(response.error || 'Failed to get user');
    }
    
    return response.data;
  },
};