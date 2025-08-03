import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { RegisterForm } from '../features/auth/components/RegisterForm';

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();

  const handleRegisterSuccess = () => {
    navigate('/login', { 
      state: { message: 'Account created successfully! Please sign in.' }
    });
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h1 className="text-center text-3xl font-bold text-gray-900 mb-8">
          Flashcard App
        </h1>
        
        <RegisterForm onSuccess={handleRegisterSuccess} />
        
        <div className="mt-6 text-center">
          <p className="text-sm text-gray-600">
            Already have an account?{' '}
            <Link
              to="/login"
              className="font-medium text-primary-600 hover:text-primary-500"
            >
              Sign in here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};