import { Component } from '@angular/core';
import { type ComponentFixture, TestBed } from '@angular/core/testing';
import { UiButtonDirective } from './button/button';
import { UiInputDirective } from './input/input';

@Component({
  template: `
    <input id="campo" uiInput placeholder="Nome" />
    <input id="campo-busca" uiInput extraClasses="pl-9" type="search" />
    <button id="botao" uiButton variant="destructive" size="sm">Excluir</button>
  `,
  imports: [UiInputDirective, UiButtonDirective],
})
class UiHostComponent {}

describe('Diretivas shadcn/ui', () => {
  let fixture: ComponentFixture<UiHostComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({ imports: [UiHostComponent] });
    await TestBed.compileComponents();
    fixture = TestBed.createComponent(UiHostComponent);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('uiInput aplica as classes de estilo no elemento', () => {
    const campo = fixture.nativeElement.querySelector('#campo') as HTMLInputElement;
    expect(campo.classList).toContain('border-input');
    expect(campo.classList).toContain('rounded-lg');
    expect(campo.classList).toContain('h-11');
    expect(campo.classList).toContain('bg-card');
    expect(campo.classList).toContain('focus-visible:ring-[3px]');
  });

  it('uiInput preserva e mescla classes extras (ex.: padding da lupa)', () => {
    const campo = fixture.nativeElement.querySelector('#campo-busca') as HTMLInputElement;
    expect(campo.classList).toContain('pl-9');
    expect(campo.classList).toContain('border-input');
  });

  it('uiButton aplica as classes do variant e do tamanho', () => {
    const botao = fixture.nativeElement.querySelector('#botao') as HTMLButtonElement;
    expect(botao.classList).toContain('bg-destructive');
    expect(botao.classList).toContain('h-8');
    expect(botao.classList).toContain('cursor-pointer');
  });
});
