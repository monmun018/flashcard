import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button, Input, Card, Alert } from '../../../components/ui';
import { useAuth } from '../hooks/useAuth';
import type { LoginRequest } from '../../../shared/types';

const loginSchema = z.object({
  loginId: z.string()
    .min(1, 'Vui lòng nhập tên đăng nhập')
    .min(3, 'Tên đăng nhập phải có ít nhất 3 ký tự'),
  password: z.string()
    .min(1, 'Vui lòng nhập mật khẩu')
    .min(6, 'Mật khẩu phải có ít nhất 6 ký tự'),
});

type LoginFormData = z.infer<typeof loginSchema>;

interface LoginFormProps {
  onSuccess?: () => void;
}

export const LoginForm: React.FC<LoginFormProps> = ({ onSuccess }) => {
  const { login, isLoading, loginError } = useAuth();
  const [showError, setShowError] = useState(false);
  const [attemptCount, setAttemptCount] = useState(0);
  
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    clearErrors,
    setFocus,
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    mode: 'onBlur', // Validate on blur for better UX
  });

  // Show error when loginError changes
  useEffect(() => {
    if (loginError) {
      setShowError(true);
      setAttemptCount(prev => prev + 1);
    }
  }, [loginError]);

  // Clear error when user starts typing
  const handleInputChange = () => {
    if (showError) {
      setShowError(false);
      clearErrors();
    }
  };

  const onSubmit = async (data: LoginFormData) => {
    try {
      setShowError(false);
      await login(data as LoginRequest);
      onSuccess?.();
    } catch (error) {
      // Error is handled by the hook and will show via useEffect
      setFocus('loginId'); // Focus back to login field for retry
    }
  };

  // User icon
  const UserIcon = () => (
    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
    </svg>
  );

  // Lock icon  
  const LockIcon = () => (
    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
    </svg>
  );

  const getErrorTitle = () => {
    // Generic error title for all authentication failures (security enhancement)
    return 'Đăng nhập thất bại';
  };

  const getErrorAdvice = () => {
    // Generic advice that doesn't reveal whether username or password is incorrect
    return 'Vui lòng kiểm tra lại thông tin đăng nhập của bạn và thử lại.';
  };

  return (
    <Card className="w-full max-w-md mx-auto p-8 shadow-lg">
      <div className="text-center mb-8">
        <div className="mx-auto w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
          <svg className="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.746 0 3.332.477 4.5 1.253v13C19.832 18.477 18.246 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
          </svg>
        </div>
        <h2 className="text-3xl font-bold text-gray-900 mb-2">Đăng nhập</h2>
        <p className="text-gray-600">Chào mừng bạn trở lại với Flashcard App</p>
      </div>

      {/* Error Alert */}
      {showError && loginError && (
        <Alert 
          variant="error" 
          title={getErrorTitle()}
          className="mb-6"
          onClose={() => setShowError(false)}
        >
          <div className="space-y-2">
            <p>{getErrorAdvice()}</p>
            {attemptCount >= 3 && (
              <p className="text-sm font-medium">
                💡 <strong>Gợi ý:</strong> Hãy chắc chắn bạn đang sử dụng đúng tài khoản và mật khẩu.
              </p>
            )}
          </div>
        </Alert>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <Input
          label="Tên đăng nhập"
          type="text"
          leftIcon={<UserIcon />}
          {...register('loginId', {
            onChange: handleInputChange
          })}
          error={errors.loginId?.message}
          placeholder="Nhập tên đăng nhập của bạn"
          autoComplete="username"
          required
          disabled={isLoading || isSubmitting}
        />

        <Input
          label="Mật khẩu"
          type="password"
          leftIcon={<LockIcon />}
          showPasswordToggle
          {...register('password', {
            onChange: handleInputChange
          })}
          error={errors.password?.message}
          placeholder="Nhập mật khẩu của bạn"
          autoComplete="current-password"
          required
          disabled={isLoading || isSubmitting}
        />

        <div className="flex items-center justify-between text-sm">
          <label className="flex items-center">
            <input
              type="checkbox"
              className="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
              disabled={isLoading || isSubmitting}
            />
            <span className="ml-2 text-gray-600">Ghi nhớ đăng nhập</span>
          </label>
          <a href="#" className="text-blue-600 hover:text-blue-500 font-medium">
            Quên mật khẩu?
          </a>
        </div>

        <Button
          type="submit"
          className="w-full py-3 text-base font-medium"
          isLoading={isLoading || isSubmitting}
          disabled={isLoading || isSubmitting}
        >
          {(isLoading || isSubmitting) ? (
            <div className="flex items-center justify-center">
              <svg className="animate-spin -ml-1 mr-3 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Đang đăng nhập...
            </div>
          ) : (
            'Đăng nhập'
          )}
        </Button>
      </form>

      <div className="mt-6 text-center">
        <p className="text-sm text-gray-600">
          Bạn gặp khó khăn khi đăng nhập?{' '}
          <a href="#" className="text-blue-600 hover:text-blue-500 font-medium">
            Liên hệ hỗ trợ
          </a>
        </p>
      </div>
    </Card>
  );
};