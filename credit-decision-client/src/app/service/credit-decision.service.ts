import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

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

    const url = environment.apiBaseUrl + 'credit-decision';
    return this.http.get(url, {params: params});
  }
}
