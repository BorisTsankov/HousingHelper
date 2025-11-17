export interface RegisterRequest {
  email: string;
  name: string;
  password: string;
}

export interface UserResponse {
  id: number;
  email: string;
  name: string;
  createdAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}