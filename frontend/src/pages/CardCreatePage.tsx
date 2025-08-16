import React, { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { ArrowLeftIcon } from '@heroicons/react/24/outline';
import { useCards } from '../features/cards/hooks/useCards';
import { useDeck } from '../features/decks/hooks/useDecks';
import { Alert } from '../components/ui';

// Validation schema
const cardCreateSchema = z.object({
  frontContent: z
    .string()
    .min(1, 'Nội dung mặt trước là bắt buộc')
    .max(1000, 'Nội dung mặt trước không được vượt quá 1000 ký tự'),
  backContent: z
    .string()
    .min(1, 'Nội dung mặt sau là bắt buộc')
    .max(1000, 'Nội dung mặt sau không được vượt quá 1000 ký tự'),
});

type CardCreateForm = z.infer<typeof cardCreateSchema>;

interface FlashcardPreviewProps {
  frontContent: string;
  backContent: string;
}

const FlashcardPreview: React.FC<FlashcardPreviewProps> = ({ frontContent, backContent }) => {
  const [isFlipped, setIsFlipped] = useState(false);

  return (
    <div className="w-full max-w-md mx-auto">
      <h3 className="text-lg font-medium text-gray-900 mb-4">Xem trước thẻ</h3>
      <div 
        className="relative w-full h-48 cursor-pointer"
        onClick={() => setIsFlipped(!isFlipped)}
      >
        <div className={`absolute inset-0 w-full h-full transition-transform duration-500 transform-style-preserve-3d ${isFlipped ? 'rotate-y-180' : ''}`}>
          {/* Front side */}
          <div className="absolute inset-0 w-full h-full backface-hidden bg-blue-50 border-2 border-blue-200 rounded-lg p-4 flex items-center justify-center">
            <div className="text-center">
              <div className="text-sm text-blue-600 mb-2">Mặt trước</div>
              <div className="text-gray-900 whitespace-pre-wrap break-words">
                {frontContent || 'Nhập nội dung mặt trước...'}
              </div>
            </div>
          </div>
          
          {/* Back side */}
          <div className="absolute inset-0 w-full h-full backface-hidden rotate-y-180 bg-green-50 border-2 border-green-200 rounded-lg p-4 flex items-center justify-center">
            <div className="text-center">
              <div className="text-sm text-green-600 mb-2">Mặt sau</div>
              <div className="text-gray-900 whitespace-pre-wrap break-words">
                {backContent || 'Nhập nội dung mặt sau...'}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className="text-center mt-2 text-sm text-gray-500">
        Nhấp để lật thẻ
      </div>
    </div>
  );
};

export const CardCreatePage: React.FC = () => {
  const { deckId } = useParams<{ deckId: string }>();
  const navigate = useNavigate();
  const [showPreview, setShowPreview] = useState(true);
  
  const deckIdNum = deckId ? parseInt(deckId, 10) : undefined;
  const { data: deck, isLoading: isDeckLoading } = useDeck(deckIdNum);
  const { createCard, isCreating, createError } = useCards(deckIdNum);

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
    reset,
  } = useForm<CardCreateForm>({
    resolver: zodResolver(cardCreateSchema),
    defaultValues: {
      frontContent: '',
      backContent: '',
    },
  });

  const watchedFront = watch('frontContent');
  const watchedBack = watch('backContent');

  const onSubmit = async (data: CardCreateForm) => {
    if (!deckIdNum) return;

    try {
      await createCard({
        deckId: deckIdNum,
        frontContent: data.frontContent,
        backContent: data.backContent,
      });
      
      // Success - navigate back to deck detail
      navigate(`/decks/${deckIdNum}`);
    } catch (error) {
      console.error('Failed to create card:', error);
    }
  };

  const onSubmitAndAddAnother = async (data: CardCreateForm) => {
    if (!deckIdNum) return;

    try {
      await createCard({
        deckId: deckIdNum,
        frontContent: data.frontContent,
        backContent: data.backContent,
      });
      
      // Reset form for another card
      reset();
    } catch (error) {
      console.error('Failed to create card:', error);
    }
  };

  if (isDeckLoading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
            <div className="h-8 bg-gray-200 rounded w-64 animate-pulse"></div>
          </div>
        </div>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 animate-pulse">
            <div className="h-6 bg-gray-200 rounded w-1/3 mb-6"></div>
            <div className="space-y-4">
              <div className="h-32 bg-gray-200 rounded"></div>
              <div className="h-32 bg-gray-200 rounded"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!deck) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
            <Link
              to="/dashboard"
              className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 transition-colors"
            >
              <ArrowLeftIcon className="w-5 h-5" />
              <span>Quay lại Dashboard</span>
            </Link>
          </div>
        </div>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <Alert variant="error" title="Lỗi">
            <p>Không tìm thấy bộ thẻ. Vui lòng kiểm tra lại.</p>
          </Alert>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center space-x-4">
            <Link
              to={`/decks/${deckId}`}
              className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 transition-colors"
            >
              <ArrowLeftIcon className="w-5 h-5" />
              <span>Quay lại</span>
            </Link>
            <div className="text-gray-400">/</div>
            <h1 className="text-xl font-semibold text-gray-900">
              Thêm thẻ mới - {deck.name}
            </h1>
          </div>
        </div>
      </div>

      {/* Main content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Form section */}
          <div className="lg:col-span-2">
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
              <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                {/* Error display */}
                {createError && (
                  <Alert variant="error" title="Lỗi tạo thẻ">
                    <p>{(createError as any)?.message || 'Có lỗi xảy ra khi tạo thẻ. Vui lòng thử lại.'}</p>
                  </Alert>
                )}

                {/* Front content */}
                <div>
                  <label htmlFor="frontContent" className="block text-sm font-medium text-gray-700 mb-2">
                    Nội dung mặt trước *
                  </label>
                  <textarea
                    {...register('frontContent')}
                    id="frontContent"
                    rows={4}
                    className="block w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:border-blue-500 focus:outline-none focus:ring-blue-500 sm:text-sm"
                    placeholder="Nhập câu hỏi hoặc từ vựng cần học..."
                  />
                  <div className="flex justify-between items-center mt-1">
                    {errors.frontContent && (
                      <p className="text-sm text-red-600">{errors.frontContent.message}</p>
                    )}
                    <div className="text-sm text-gray-500 ml-auto">
                      {watchedFront?.length || 0}/1000
                    </div>
                  </div>
                </div>

                {/* Back content */}
                <div>
                  <label htmlFor="backContent" className="block text-sm font-medium text-gray-700 mb-2">
                    Nội dung mặt sau *
                  </label>
                  <textarea
                    {...register('backContent')}
                    id="backContent"
                    rows={4}
                    className="block w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-500 focus:border-blue-500 focus:outline-none focus:ring-blue-500 sm:text-sm"
                    placeholder="Nhập câu trả lời hoặc nghĩa của từ..."
                  />
                  <div className="flex justify-between items-center mt-1">
                    {errors.backContent && (
                      <p className="text-sm text-red-600">{errors.backContent.message}</p>
                    )}
                    <div className="text-sm text-gray-500 ml-auto">
                      {watchedBack?.length || 0}/1000
                    </div>
                  </div>
                </div>

                {/* Mobile preview toggle */}
                <div className="lg:hidden">
                  <button
                    type="button"
                    onClick={() => setShowPreview(!showPreview)}
                    className="w-full bg-gray-100 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-200 transition-colors"
                  >
                    {showPreview ? 'Ẩn xem trước' : 'Hiển thị xem trước'}
                  </button>
                </div>

                {/* Mobile preview */}
                {showPreview && (
                  <div className="lg:hidden">
                    <FlashcardPreview frontContent={watchedFront} backContent={watchedBack} />
                  </div>
                )}

                {/* Action buttons */}
                <div className="flex flex-col sm:flex-row gap-3 pt-4">
                  <button
                    type="submit"
                    disabled={isCreating}
                    className="flex-1 bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
                  >
                    {isCreating ? 'Đang lưu...' : 'Lưu và quay lại'}
                  </button>
                  
                  <button
                    type="button"
                    onClick={handleSubmit(onSubmitAndAddAnother)}
                    disabled={isCreating}
                    className="flex-1 bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
                  >
                    {isCreating ? 'Đang lưu...' : 'Lưu và tạo tiếp'}
                  </button>
                </div>
              </form>
            </div>
          </div>

          {/* Desktop preview */}
          <div className="hidden lg:block">
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 sticky top-8">
              <FlashcardPreview frontContent={watchedFront} backContent={watchedBack} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};