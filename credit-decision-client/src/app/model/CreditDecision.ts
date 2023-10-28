export class CreditDecision {
    status: 'APROOVED' | 'REJECTED' | 'UNDEFINED';
    amountRequested: number;
    periodRequested: number;
    amountAprooved: number;
    periodAprooved: number;
    msg: string;
}