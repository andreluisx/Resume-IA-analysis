package dev.andre.ResumeAiAnalysis.ImplementationAi;

import com.google.genai.types.Schema;
import dev.andre.ResumeAiAnalysis.ImplementationAi.Enums.Status;

import java.util.List;
import java.util.Map;

public class AiSchema {
    public static final Schema SCHEMA = Schema.builder()
            .type("object")
            .properties(Map.of(
                    "application_description", Schema.builder()
                            .description("Aqui voce vai informar falando diretamente com o recrutador" +
                                    " um resumo sobre o curriculo do aplicante, fatores importantes como localização (caso" +
                                    " a vaga nao seja home office), conhecimentos, tempo de estudo, habilidades, enfim deixe claro " +
                                    "para o recrutador de que tipo de pessoa se trata e se vale a pena chamar para uma entrevista")
                            .type("string").build(),
                    "improvements_to_this_vacancy", Schema.builder()
                            .description("Aqui voce vai informar falando diretamente com o dono do curriculo" +
                                    "como esta a situação do curriculo dele em relação a vaga, se ele tem chances de ser chamado para uma" +
                                    "entrevista ou não. caso ela tenha mais chances de ser chamado, fale os principais pontos que ele deve revisar" +
                                    "e tambem qual pontos que ele deve aprender pra estar 100% apto a vaga, caso as chances dele ser chamado a uma entrevista sejam baixas" +
                                    "explique da mesma forma a situação dele e pra ajuda-lo diga o que ele deve alterar no curriculo/aprender para conseguir uma aprovação nessa vaga")
                            .type("string").build(),
                    "essential_matches", Schema.builder()
                            .type("array")
                            .description("coisas que ele sabe (ou possivelmente sabe) com base no curriculo que tem no essencial da vaga")
                            .items(Schema.builder().type("string").build())
                            .build(),
                    "differential_matches", Schema.builder()
                            .type("array")
                            .description("coisas que ele sabe (ou possivelmente sabe) com base no curriculo que tem no diferencial da vaga")
                            .items(Schema.builder().type("string").build())
                            .build(),
                    "status", Schema.builder()
                            .type("string")
                            .enum_(List.of("APPROVED", "REJECTED", "NEEDS_HUMAN_REVIEW"))
                            .description("Status da análise: APPROVED (aprovado), REJECTED (rejeitado), NEEDS_HUMAN_REVIEW (necessita revisão humana)..." +
                                    "apenas rejeite se tiver menos de 50% de compatibilidade com a vaga, para mais que isso voce decide se precisa de uma revisao" +
                                    "humana ou se ele for muito apto a vaga ele ja esta aprovado")
                            .build()

            ))
            .build();
}
