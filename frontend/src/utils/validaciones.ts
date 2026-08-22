// frontend/src/utils/validaciones.ts
export const REGEX_EMAIL: RegExp = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
export const REGEX_TELEFONO: RegExp = /^\+\d{1,4} \d{4,15}$/;
export const REGEX_NOMBRE_COMPUETO: RegExp = /^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?:\s[A-Za-zÁÉÍÓÚáéíóúÑñ]+)+$/;

export function validarEmail(email: string): boolean {
  return REGEX_EMAIL.test(email.trim());
}

export function validarTelefono(telefono: string): boolean {
  return REGEX_TELEFONO.test(telefono.trim());
}

export function validarNombreCompleto(nombre: string): boolean {
  const limpio = nombre.trim();
  if (limpio.length < 2) return false;
  return REGEX_NOMBRE_COMPUETO.test(limpio);
}
