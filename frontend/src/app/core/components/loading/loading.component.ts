import { Component } from '@angular/core';
import { LoadingService } from '../../services/loading.service';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.css']
})
export class LoadingComponent {
  readonly loading$ = this.loadingService.loading$;
  readonly message$ = this.loadingService.message$;

  constructor(private readonly loadingService: LoadingService) {}
}
