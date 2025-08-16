import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useNavigate } from 'react-router-dom';
import { Button, Input, Card, Alert } from '../../../components/ui';
import { useDecks } from '../hooks/useDecks';
import type { DeckCreateRequest } from '../../../shared/types';

const deckSchema = z.object({
  deckName: z.string()
    .min(1, 'Tên bộ thẻ là bắt buộc')
    .min(3, 'Tên bộ thẻ phải có ít nhất 3 ký tự')
    .max(100, 'Tên bộ thẻ không được quá 100 ký tự')
    .trim(),
});

type DeckFormData = z.infer<typeof deckSchema>;

interface CreateDeckFormProps {
  onSuccess?: (deck: any) => void;
  onCancel?: () => void;
}

export const CreateDeckForm: React.FC<CreateDeckFormProps> = ({ 
  onSuccess, 
  onCancel 
}) => {
  const navigate = useNavigate();
  const { createDeck, isCreating, createError } = useDecks();
  const [showError, setShowError] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<DeckFormData>({
    resolver: zodResolver(deckSchema),
    mode: 'onBlur',
  });

  const onSubmit = async (data: DeckFormData) => {
    try {
      setShowError(false);
      const deck = await createDeck(data as DeckCreateRequest);
      reset();
      
      if (onSuccess) {
        onSuccess(deck);
      } else {
        navigate(`/decks/${deck.id}`);
      }
    } catch (error) {
      setShowError(true);
    }
  };

  const handleCancel = () => {
    reset();
    if (onCancel) {
      onCancel();
    } else {
      navigate('/dashboard');
    }
  };

  // Deck icon
  const DeckIcon = () => (
    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
    </svg>
  );

  return (
    <Card className="w-full max-w-lg mx-auto p-8">
      <div className="text-center mb-8">
        <div className="mx-auto w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4">
          <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
        </div>
        <h2 className="text-3xl font-bold text-gray-900 mb-2">Tạo bộ thẻ mới</h2>
        <p className="text-gray-600">Bắt đầu hành trình học tập của bạn</p>
      </div>

      {/* Error Alert */}
      {showError && createError && (
        <Alert 
          variant="error" 
          title="Không thể tạo bộ thẻ"
          className="mb-6"
          onClose={() => setShowError(false)}
        >
          <p>{(createError as any)?.message || 'Đã có lỗi xảy ra. Vui lòng thử lại.'}</p>
        </Alert>
      )}

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <Input
          label="Tên bộ thẻ"
          type="text"
          leftIcon={<DeckIcon />}
          {...register('deckName')}
          error={errors.deckName?.message}
          placeholder="Nhập tên bộ thẻ (VD: Tiếng Anh cơ bản)"
          autoComplete="off"
          required
          disabled={isCreating || isSubmitting}
          autoFocus
        />

        <div className="text-sm text-gray-500 bg-blue-50 p-3 rounded-lg">
          <div className="flex items-start space-x-2">
            <svg className="w-4 h-4 text-blue-500 mt-0.5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
            </svg>
            <div>
              <p className="font-medium text-blue-800">Mẹo tạo bộ thẻ hiệu quả:</p>
              <ul className="mt-1 text-blue-700 space-y-1">
                <li>• Đặt tên mô tả rõ nội dung (VD: "Từ vựng IELTS Reading")</li>
                <li>• Nhóm theo chủ đề hoặc cấp độ</li>
                <li>• Tránh tên quá dài hoặc quá ngắn</li>
              </ul>
            </div>
          </div>
        </div>

        <div className="flex space-x-4">
          <Button
            type="button"
            variant="secondary"
            className="flex-1"
            onClick={handleCancel}
            disabled={isCreating || isSubmitting}
          >
            Hủy
          </Button>
          
          <Button
            type="submit"
            className="flex-1"
            isLoading={isCreating || isSubmitting}
            disabled={isCreating || isSubmitting}
          >
            {(isCreating || isSubmitting) ? (
              <div className="flex items-center justify-center">
                <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Đang tạo...
              </div>
            ) : (
              'Tạo bộ thẻ'
            )}
          </Button>
        </div>
      </form>

      <div className="mt-6 text-center">
        <p className="text-sm text-gray-500">
          Sau khi tạo, bạn có thể thêm thẻ học ngay lập tức
        </p>
      </div>
    </Card>
  );
};