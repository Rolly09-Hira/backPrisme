package com.prisme.back.exception;

public class PaiementException extends RuntimeException {
    public PaiementException(String message) {
        super(message);
    }

    public static class NotFoundException extends PaiementException {
        public NotFoundException(Long id) {
            super("Paiement non trouvé avec l'ID: " + id);
        }

        public NotFoundException(String reference) {
            super("Paiement non trouvé avec la référence: " + reference);
        }
    }

    public static class AlreadyValidatedException extends PaiementException {
        public AlreadyValidatedException() {
            super("Ce paiement a déjà été validé");
        }
    }

    public static class InvalidAmountException extends PaiementException {
        public InvalidAmountException() {
            super("Le montant du paiement est invalide");
        }
    }

    public static class DuplicateReferenceException extends PaiementException {
        public DuplicateReferenceException(String reference) {
            super("Un paiement avec la référence " + reference + " existe déjà");
        }
    }
}