export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  username: string;
  expiresInSeconds: number;
}

export interface AuthSession {
  token: string;
  username: string;
  expiresAt: number;
}
