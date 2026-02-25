import api, { setAccessToken } from "./api";

export interface RegisterData {
  username: string;
  email: string;
  password: string;
  fullName?: string;
}

export interface LoginData {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  user: {
    id: number;
    username: string;
    email: string;
    fullName?: string;
    emailVerified: boolean;
    enabled: boolean;
  };
}

export const authService = {
  register: async (data: RegisterData): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>("/auth/register", data);

    // Store AT in memory
    setAccessToken(response.data.accessToken);

    return response.data;
  },

  login: async (data: LoginData): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>("/auth/login", data);
    setAccessToken(response.data.accessToken);
    return response.data;
  },

  logout: async (): Promise<void> => {
    await api.post("/auth/logout");

    // remove AT from memory
    setAccessToken(null);
  },

  refreshToken: async (): Promise<string> => {
    const response = await api.post<{ accessToken: string }>("/auth/refresh");
    // Update AT in memory
    setAccessToken(response.data.accessToken);
    return response.data.accessToken;
  },
};
