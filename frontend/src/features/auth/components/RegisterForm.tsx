import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button, Input, Card } from '../../../components/ui';
import { useAuth } from '../hooks/useAuth';
import type { RegisterRequest } from '../../../shared/types';

const registerSchema = z.object({
  loginId: z.string().min(1, 'Login ID is required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  name: z.string().min(1, 'Name is required'),
  age: z.number().min(1, 'Age must be at least 1').max(150, 'Age must not exceed 150').optional(),
  email: z.string().email('Invalid email').optional().or(z.literal('')),
});

type RegisterFormData = z.infer<typeof registerSchema>;

interface RegisterFormProps {
  onSuccess?: () => void;
}

export const RegisterForm: React.FC<RegisterFormProps> = ({ onSuccess }) => {
  const { register: registerUser, isLoading, registerError } = useAuth();
  
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    try {
      const registerData: RegisterRequest = {
        ...data,
        email: data.email || undefined,
      };
      await registerUser(registerData);
      onSuccess?.();
    } catch (error) {
      // Error is handled by the hook
    }
  };

  return (
    <Card className="w-full max-w-md mx-auto">
      <div className="text-center mb-6">
        <h2 className="text-2xl font-bold text-gray-900">Create Account</h2>
        <p className="text-gray-600 mt-2">Join Flashcard App today</p>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="Login ID"
          type="text"
          {...register('loginId')}
          error={errors.loginId?.message}
          placeholder="Choose a login ID"
        />

        <Input
          label="Password"
          type="password"
          {...register('password')}
          error={errors.password?.message}
          placeholder="Create a password"
        />

        <Input
          label="Full Name"
          type="text"
          {...register('name')}
          error={errors.name?.message}
          placeholder="Enter your full name"
        />

        <Input
          label="Age (optional)"
          type="number"
          {...register('age', { valueAsNumber: true })}
          error={errors.age?.message}
          placeholder="Enter your age"
        />

        <Input
          label="Email (optional)"
          type="email"
          {...register('email')}
          error={errors.email?.message}
          placeholder="Enter your email"
        />

        {registerError && (
          <div className="text-sm text-red-600 text-center">
            {registerError.message || 'Registration failed. Please try again.'}
          </div>
        )}

        <Button
          type="submit"
          className="w-full"
          isLoading={isLoading}
          disabled={isLoading}
        >
          {isLoading ? 'Creating account...' : 'Create Account'}
        </Button>
      </form>
    </Card>
  );
};