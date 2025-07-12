import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionprotocolComponent } from './transactionprotocol.component';

describe('TransactionprotocolComponent', () => {
  let component: TransactionprotocolComponent;
  let fixture: ComponentFixture<TransactionprotocolComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionprotocolComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransactionprotocolComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
