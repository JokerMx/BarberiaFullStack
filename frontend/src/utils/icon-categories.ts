// frontend/src/utils/icon-categories.ts
import { CategoriaServicio } from '../enums/categorias.enum';

export const ICONOS_CATEGORIA: Record<CategoriaServicio, string> = {
  [CategoriaServicio.CORTE]: '✂️',
  [CategoriaServicio.TINTE]: '🎨',
  [CategoriaServicio.TRATAMIENTO]: '🧴',
  [CategoriaServicio.PEINADO]: '💨',
  [CategoriaServicio.MAQUILLAJE]: '💄',
  [CategoriaServicio.BARBA]: '🧔',
  [CategoriaServicio.DEPILACION]: '🌸'
};

export function obtenerIconoCategoria(categoria: string): string {
  return ICONOS_CATEGORIA[categoria as CategoriaServicio] || '💎';
}
