import { forwardRef, useState } from 'react';
import type { InputHTMLAttributes } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  showPasswordToggle?: boolean;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ 
    label, 
    error, 
    helperText, 
    leftIcon, 
    rightIcon, 
    showPasswordToggle, 
    type = 'text',
    className = '', 
    ...props 
  }, ref) => {
    const [showPassword, setShowPassword] = useState(false);
    const [isFocused, setIsFocused] = useState(false);

    const inputType = showPasswordToggle && type === 'password' 
      ? (showPassword ? 'text' : 'password') 
      : type;

    const baseClasses = `
      block w-full px-4 py-3 text-sm text-gray-900 placeholder-gray-500
      bg-white border rounded-lg transition-all duration-200
      focus:outline-none focus:ring-2 focus:ring-offset-1
      disabled:bg-gray-50 disabled:text-gray-500 disabled:cursor-not-allowed
    `;

    const stateClasses = error 
      ? 'border-red-300 focus:border-red-500 focus:ring-red-200' 
      : isFocused
        ? 'border-blue-500 focus:border-blue-500 focus:ring-blue-200'
        : 'border-gray-300 hover:border-gray-400 focus:border-blue-500 focus:ring-blue-200';

    const paddingClasses = `
      ${leftIcon ? 'pl-11' : 'pl-4'}
      ${rightIcon || showPasswordToggle ? 'pr-11' : 'pr-4'}
    `;

    const inputClasses = `${baseClasses} ${stateClasses} ${paddingClasses} ${className}`;

    const EyeIcon = ({ closed = false }: { closed?: boolean }) => (
      <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        {closed ? (
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
        ) : (
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        )}
      </svg>
    );

    return (
      <div className="w-full">
        {label && (
          <label className={`block text-sm font-medium mb-2 transition-colors duration-200 ${
            error ? 'text-red-700' : 'text-gray-700'
          }`}>
            {label}
            {props.required && <span className="text-red-500 ml-1">*</span>}
          </label>
        )}
        
        <div className="relative">
          {leftIcon && (
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <div className={`text-gray-400 ${error ? 'text-red-400' : ''}`}>
                {leftIcon}
              </div>
            </div>
          )}
          
          <input
            ref={ref}
            type={inputType}
            className={inputClasses}
            onFocus={(e) => {
              setIsFocused(true);
              props.onFocus?.(e);
            }}
            onBlur={(e) => {
              setIsFocused(false);
              props.onBlur?.(e);
            }}
            {...props}
          />
          
          {(rightIcon || showPasswordToggle) && (
            <div className="absolute inset-y-0 right-0 pr-3 flex items-center">
              {showPasswordToggle && type === 'password' ? (
                <button
                  type="button"
                  className={`text-gray-400 hover:text-gray-600 focus:outline-none focus:text-gray-600 transition-colors duration-200 ${
                    error ? 'text-red-400 hover:text-red-600' : ''
                  }`}
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={showPassword ? 'Ẩn mật khẩu' : 'Hiện mật khẩu'}
                >
                  <EyeIcon closed={showPassword} />
                </button>
              ) : (
                <div className={`text-gray-400 ${error ? 'text-red-400' : ''}`}>
                  {rightIcon}
                </div>
              )}
            </div>
          )}
        </div>
        
        {error && (
          <div className="mt-2 flex items-start space-x-1">
            <svg className="w-4 h-4 text-red-500 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
            </svg>
            <p className="text-sm text-red-600">{error}</p>
          </div>
        )}
        
        {helperText && !error && (
          <p className="mt-2 text-sm text-gray-500">{helperText}</p>
        )}
      </div>
    );
  }
);

Input.displayName = 'Input';