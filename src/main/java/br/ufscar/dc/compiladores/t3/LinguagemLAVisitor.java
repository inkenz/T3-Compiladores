package br.ufscar.dc.compiladores.t3;

import java.util.LinkedList;
public class LinguagemLAVisitor extends LABaseVisitor<Void>{
    Escopo escopo = new Escopo();

    @Override
    public Void visitDeclaracoes(LAParser.DeclaracoesContext ctx) 
    {
        escopo.criarNovoEscopo();
        return super.visitDeclaracoes(ctx);
    }

    @Override
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx)
    {
        TabelaDeSimbolos tabelaAtual = escopo.escopoAtual();

        if (ctx.PROCEDIMENTO() != null){
            String nome = ctx.PROCEDIMENTO().getText();

            // TODO: Colocar erro semântico caso o nome do procedimento já exista.
            if (tabelaAtual.existe(nome)){
                
            }
        }
        else if (ctx.FUNCAO() != null){
            String nome = ctx.FUNCAO().getText();
            
            // TODO: Colocar erro semântico caso o nome do procedimento já exista.
            if (tabelaAtual.existe(nome)){
            }
        }

        return super.visitDeclaracao_global(ctx);
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx)
    {
        TabelaDeSimbolos tabela = escopo.escopoAtual();

        if (ctx.DECLARE() != null){
            String nome = ctx.DECLARE().getText();

            // TODO: Colocar erro que já exista a variável.
            if (tabela.existe(nome)){
                System.out.println("Variavel " + nome + "ja esta declarada");
            }
            else{
                LinguagemLAUtils.verificarTipo(tabela, ctx.variavel());
            }
        }
        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) 
    {
        LinkedList<TabelaDeSimbolos> tabelas = escopo.recuperarTodosEscopos();
        String nome = ctx.IDENT().get(0).getText();
        boolean existeVariavel = false;

        for ( TabelaDeSimbolos tabela: tabelas){
            if (tabela.existe(nome)){
                existeVariavel = true;
                break;
            }
        }

        if (!existeVariavel){
            LinguagemLAUtils.adicionarErroSemantico(ctx.start, "identificador " + nome + " nao declarado" );
        }

        return super.visitIdentificador(ctx);
    }
}
