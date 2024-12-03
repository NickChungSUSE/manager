import { Injectable } from '@angular/core';
import {
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { v4 as uuidv4 } from 'uuid';
import { GlobalConstant } from '@common/constants/global.constant';

@Injectable()
export class RequestInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler) {
    return next.handle(
      req.clone({
        headers: req.headers.set(GlobalConstant.CORRELATION_ID, uuidv4()),
      })
    );
  }
}
