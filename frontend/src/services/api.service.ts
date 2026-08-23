// frontend/src/services/api.service.ts
export class ApiService {
  private static baseUrl: string = import.meta.env.VITE_API_URL || '/api';
  private static timeout: number = parseInt(import.meta.env.VITE_API_TIMEOUT || '10000');

  static async fetch<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.timeout);

    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...options.headers
        },
        signal: controller.signal
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
      }

      if (response.status === 204) return undefined as T;
      const body = await response.text();
      return (body ? JSON.parse(body) : undefined) as T;
    } catch (error) {
      if ((error as Error).name === 'AbortError') {
        throw new Error('La solicitud ha excedido el tiempo de espera');
      }
      throw error;
    }
  }
}