import React from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { ArrowLeftIcon, PlusIcon } from '@heroicons/react/24/outline';
import { useQuery } from '@tanstack/react-query';
import { deckService } from '../features/decks/services/deckService';
import { useCards } from '../features/cards/hooks/useCards';
import { Alert } from '../components/ui';

export const DeckDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const deckId = id ? parseInt(id, 10) : null;

  const { data: deck, isLoading, error } = useQuery({
    queryKey: ['deck', deckId],
    queryFn: () => deckService.getDeckById(deckId!),
    enabled: deckId !== null,
  });

  const { cards, isLoading: isCardsLoading } = useCards(deckId || undefined);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="py-6">
              <div className="flex items-center space-x-4">
                <div className="w-8 h-8 bg-gray-200 rounded animate-pulse"></div>
                <div className="h-8 bg-gray-200 rounded w-48 animate-pulse"></div>
              </div>
            </div>
          </div>
        </div>
        
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 animate-pulse">
            <div className="h-6 bg-gray-200 rounded w-1/3 mb-4"></div>
            <div className="h-4 bg-gray-200 rounded w-2/3 mb-6"></div>
            <div className="grid grid-cols-3 gap-4 mb-6">
              <div className="h-20 bg-gray-200 rounded"></div>
              <div className="h-20 bg-gray-200 rounded"></div>
              <div className="h-20 bg-gray-200 rounded"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !deck) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="py-6">
              <button
                onClick={() => navigate('/dashboard')}
                className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 transition-colors"
              >
                <ArrowLeftIcon className="w-5 h-5" />
                <span>Quay lại Dashboard</span>
              </button>
            </div>
          </div>
        </div>
        
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <Alert variant="error" title="Lỗi tải dữ liệu">
            <p>Không thể tải thông tin bộ thẻ. Vui lòng thử lại.</p>
          </Alert>
        </div>
      </div>
    );
  }

  const totalCards = deck.newCardNum + deck.learningCardNum + deck.dueCardNum;
  const progressPercentage = totalCards > 0 ? Math.round((deck.learningCardNum / totalCards) * 100) : 0;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="py-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <button
                  onClick={() => navigate('/dashboard')}
                  className="flex items-center space-x-2 text-gray-600 hover:text-gray-900 transition-colors"
                >
                  <ArrowLeftIcon className="w-5 h-5" />
                  <span>Quay lại Dashboard</span>
                </button>
              </div>
              
              <div className="flex items-center space-x-3">
                <Link
                  to={`/decks/${deck.id}/study`}
                  className="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition-colors font-medium"
                >
                  Học ngay
                </Link>
                <Link
                  to={`/decks/${deck.id}/cards/create`}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors font-medium flex items-center space-x-2"
                >
                  <PlusIcon className="w-4 h-4" />
                  <span>Thêm thẻ</span>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Deck info card */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-8">
          <div className="flex items-start justify-between mb-6">
            <div className="flex-1">
              <h1 className="text-3xl font-bold text-gray-900 mb-2">{deck.name}</h1>
              <p className="text-gray-600">
                {deck.createdDate ? (
                  `Tạo lúc: ${new Date(deck.createdDate).toLocaleDateString('vi-VN', {
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                  })}`
                ) : (
                  'Bộ thẻ học tập'
                )}
              </p>
            </div>
            
            <div className="text-right">
              <div className="text-4xl font-bold text-gray-900">{totalCards}</div>
              <div className="text-lg text-gray-500">thẻ</div>
            </div>
          </div>

          {/* Statistics */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
            <div className="bg-blue-50 rounded-lg p-4 border border-blue-200">
              <div className="flex items-center space-x-3">
                <div className="w-4 h-4 bg-blue-500 rounded-full"></div>
                <div>
                  <div className="text-2xl font-bold text-blue-700">{deck.newCardNum}</div>
                  <div className="text-sm text-blue-600">Thẻ mới</div>
                </div>
              </div>
            </div>
            
            <div className="bg-yellow-50 rounded-lg p-4 border border-yellow-200">
              <div className="flex items-center space-x-3">
                <div className="w-4 h-4 bg-yellow-500 rounded-full"></div>
                <div>
                  <div className="text-2xl font-bold text-yellow-700">{deck.learningCardNum}</div>
                  <div className="text-sm text-yellow-600">Đang học</div>
                </div>
              </div>
            </div>
            
            <div className="bg-red-50 rounded-lg p-4 border border-red-200">
              <div className="flex items-center space-x-3">
                <div className="w-4 h-4 bg-red-500 rounded-full"></div>
                <div>
                  <div className="text-2xl font-bold text-red-700">{deck.dueCardNum}</div>
                  <div className="text-sm text-red-600">Cần ôn tập</div>
                </div>
              </div>
            </div>
          </div>

          {/* Progress bar */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm font-medium text-gray-700">Tiến độ học tập</span>
              <span className="text-sm text-gray-500">{progressPercentage}%</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-3">
              <div 
                className="bg-green-600 h-3 rounded-full transition-all duration-300"
                style={{ width: `${progressPercentage}%` }}
              ></div>
            </div>
          </div>
        </div>

        {/* Cards section */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Danh sách thẻ</h2>
            <Link
              to={`/decks/${deck.id}/cards/create`}
              className="text-blue-600 hover:text-blue-700 text-sm font-medium"
            >
              + Thêm thẻ mới
            </Link>
          </div>

          {isCardsLoading ? (
            <div className="space-y-4">
              {[...Array(3)].map((_, index) => (
                <div key={index} className="bg-gray-100 rounded-lg p-4 animate-pulse">
                  <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                  <div className="h-4 bg-gray-200 rounded w-1/2"></div>
                </div>
              ))}
            </div>
          ) : cards.length === 0 ? (
            <div className="text-center py-12">
              <div className="mx-auto w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mb-6">
                <svg className="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 4V2a1 1 0 011-1h8a1 1 0 011 1v2h4a1 1 0 110 2h-1v12a2 2 0 01-2 2H6a2 2 0 01-2-2V6H3a1 1 0 110-2h4zM6 6v12h12V6H6zm3 3a1 1 0 112 0v6a1 1 0 11-2 0V9zm4 0a1 1 0 112 0v6a1 1 0 11-2 0V9z" />
                </svg>
              </div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">Chưa có thẻ nào</h3>
              <p className="text-gray-600 mb-6">Tạo thẻ đầu tiên để bắt đầu học!</p>
              <Link
                to={`/decks/${deck.id}/cards/create`}
                className="inline-flex items-center space-x-2 bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors font-medium"
              >
                <PlusIcon className="w-5 h-5" />
                <span>Tạo thẻ đầu tiên</span>
              </Link>
            </div>
          ) : (
            <div className="space-y-4">
              <div className="text-center py-4 mb-4">
                <p className="text-gray-600 mb-4">Hiển thị {cards.length} thẻ trong bộ thẻ</p>
                <div className="flex justify-center space-x-4">
                  <Link
                    to={`/decks/${deck.id}/study`}
                    className="bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition-colors font-medium"
                  >
                    Bắt đầu học
                  </Link>
                  <Link
                    to={`/decks/${deck.id}/cards/create`}
                    className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors font-medium"
                  >
                    Thêm thẻ mới
                  </Link>
                </div>
              </div>
              
              {/* Card list */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {cards.map((card) => (
                  <div key={card.id} className="bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                    <div className="space-y-3">
                      <div>
                        <div className="text-xs text-gray-500 mb-1">Mặt trước</div>
                        <div className="text-gray-900 text-sm">{card.frontContent}</div>
                      </div>
                      <div>
                        <div className="text-xs text-gray-500 mb-1">Mặt sau</div>
                        <div className="text-gray-700 text-sm">{card.backContent}</div>
                      </div>
                      <div className="flex items-center justify-between text-xs text-gray-500">
                        <span>Status: {card.status === 0 ? 'Mới' : card.status === 1 ? 'Đang học' : 'Cần ôn'}</span>
                        <span>ID: {card.id}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};