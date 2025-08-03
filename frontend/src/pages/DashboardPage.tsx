import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, Button } from '../components/ui';
import { useAuth } from '../features/auth/hooks/useAuth';
import { deckService } from '../features/decks/services/deckService';
import type { Deck } from '../shared/types';

export const DashboardPage: React.FC = () => {
  const { user, logout } = useAuth();
  
  const { data: decks, isLoading, error } = useQuery({
    queryKey: ['decks'],
    queryFn: deckService.getDecks,
  });

  const handleLogout = () => {
    logout();
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading your decks...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <Card className="text-center">
          <p className="text-red-600 mb-4">Failed to load decks</p>
          <Button onClick={() => window.location.reload()}>
            Try Again
          </Button>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">
                Welcome back, {user?.name}!
              </h1>
              <p className="text-gray-600">Ready to study your flashcards?</p>
            </div>
            <Button variant="outline" onClick={handleLogout}>
              Sign Out
            </Button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          {/* Quick Stats */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <Card>
              <h3 className="text-lg font-medium text-gray-900">Total Decks</h3>
              <p className="text-3xl font-bold text-primary-600 mt-2">
                {decks?.length || 0}
              </p>
            </Card>
            <Card>
              <h3 className="text-lg font-medium text-gray-900">Cards to Review</h3>
              <p className="text-3xl font-bold text-orange-600 mt-2">
                {decks?.reduce((total, deck) => total + deck.dueCardNum, 0) || 0}
              </p>
            </Card>
            <Card>
              <h3 className="text-lg font-medium text-gray-900">Learning Cards</h3>
              <p className="text-3xl font-bold text-green-600 mt-2">
                {decks?.reduce((total, deck) => total + deck.learningCardNum, 0) || 0}
              </p>
            </Card>
          </div>

          {/* Decks Section */}
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold text-gray-900">Your Decks</h2>
            <Button>Create New Deck</Button>
          </div>

          {decks && decks.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {decks.map((deck: Deck) => (
                <Card key={deck.id} className="hover:shadow-lg transition-shadow cursor-pointer">
                  <h3 className="text-xl font-semibold text-gray-900 mb-2">
                    {deck.name}
                  </h3>
                  <div className="space-y-2 text-sm text-gray-600">
                    <div className="flex justify-between">
                      <span>New cards:</span>
                      <span className="font-medium">{deck.newCardNum}</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Learning:</span>
                      <span className="font-medium text-green-600">{deck.learningCardNum}</span>
                    </div>
                    <div className="flex justify-between">
                      <span>Due:</span>
                      <span className="font-medium text-orange-600">{deck.dueCardNum}</span>
                    </div>
                  </div>
                  <div className="mt-4 flex gap-2">
                    <Button size="sm" className="flex-1">
                      Study
                    </Button>
                    <Button size="sm" variant="outline" className="flex-1">
                      Edit
                    </Button>
                  </div>
                </Card>
              ))}
            </div>
          ) : (
            <Card className="text-center py-12">
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                No decks yet
              </h3>
              <p className="text-gray-600 mb-6">
                Create your first deck to start studying with flashcards!
              </p>
              <Button>Create Your First Deck</Button>
            </Card>
          )}
        </div>
      </main>
    </div>
  );
};