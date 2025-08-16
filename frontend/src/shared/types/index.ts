// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  error?: string;
  timestamp: string;
}

// Authentication Types
export interface LoginRequest {
  loginId: string;
  password: string;
}

export interface RegisterRequest {
  loginId: string;
  password: string;
  name: string;
  age?: number;
  email?: string;
}

export interface User {
  id: number;
  loginId: string;
  name: string;
  age?: number;
  email?: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  user: User;
}

// Card Types
export interface Card {
  id: number;
  deckId: number;
  frontContent: string;
  backContent: string;
  remindTime: string;
  status: number;
}

export interface CardCreateRequest {
  deckId: number;
  frontContent: string;
  backContent: string;
}

// Deck Types
export interface Deck {
  id: number;
  userId: number;
  name: string;
  newCardNum: number;
  learningCardNum: number;
  dueCardNum: number;
  createdDate?: string;
}

export interface DeckCreateRequest {
  deckName: string;
}

// Common Types
export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
}

export interface ErrorResponse {
  message: string;
  code?: string;
  details?: Record<string, string>;
}