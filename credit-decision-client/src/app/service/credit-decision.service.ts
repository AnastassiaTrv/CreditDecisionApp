import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CreditDecisionService {

  constructor(private http: HttpClient) { }

  getCreditDecision(customerId: string, amount: number, period: number) {
    let params = new HttpParams()
    .set("customerId", customerId)
    .set("amount", amount.toString())
    .set("period", period.toString())

    return this.http.get('http://localhost:8080/api/credit-decision', {params: params});
  }
}
