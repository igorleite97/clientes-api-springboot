package br.com.seatecnologia.clientesapi.model;

/**
 * Tipos de telefone aceitos pelo sistema.
 *
 * Cada tipo define o formato da máscara que será aplicada na resposta:
 *   - RESIDENCIAL e COMERCIAL: (XX) XXXX-XXXX  → 10 dígitos
 *   - CELULAR:                 (XX) XXXXX-XXXX  → 11 dígitos (um dígito a mais)
 *
 * Esse enum vai direto para o banco como texto (STRING),
 * o que facilita a leitura nas queries do H2.
 */
public enum TipoTelefone {
    RESIDENCIAL,
    COMERCIAL,
    CELULAR
}