package dev.andre.ResumeAiAnalysis.ImplementationAi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.*;
import dev.andre.ResumeAiAnalysis.Auth.Exceptions.UnauthenticatedUser;
import dev.andre.ResumeAiAnalysis.Enums.VacancyRole;
import dev.andre.ResumeAiAnalysis.ExceptionHandler.NotFoundException;
import dev.andre.ResumeAiAnalysis.ImplementationAi.Exceptions.AiProcessingException;
import dev.andre.ResumeAiAnalysis.ImplementationAi.Exceptions.PdfProcessingException;
import dev.andre.ResumeAiAnalysis.ImplementationAi.Responses.VacancyToAi;
import dev.andre.ResumeAiAnalysis.User.UserEntity;
import dev.andre.ResumeAiAnalysis.User.UserService;
import dev.andre.ResumeAiAnalysis.Vacancy.Exceptions.UserCannotAccessOrDoThat;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyEntity;
import dev.andre.ResumeAiAnalysis.VacancyUser.UserVacancyService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Service
public class AiService {

    final private AiRepository aiRepository;
    final private UserService userService;
    final private UserVacancyService userVacancyService;

    public AiService(AiRepository aiRepository, UserService userService, UserVacancyService userVacancyService) {
        this.userService = userService;
        this.aiRepository = aiRepository;
        this.userVacancyService = userVacancyService;
    }

    public AIEntity save(AIEntity aiEntity) {
        return aiRepository.save(aiEntity);
    }

    private String googleApiKey = System.getenv("GOOGLE_API_KEY");
    private ObjectMapper objectMapper = new ObjectMapper();
    Client client = Client.builder().apiKey(googleApiKey).build();

    /**
     * Analisa um currículo em PDF em relação a uma vaga
     *
     * @param pdfFile Arquivo PDF do currículo
     * @return AIEntity com a análise
     */
    public AIEntity gerarTextoComPdf(
            File pdfFile,
            VacancyToAi vacancy,
            UserVacancyEntity userVacancyEntity
    ) {
        try {
            // ====== 1. Leitura do PDF ======
            byte[] fileBytes;
            try {
                fileBytes = Files.readAllBytes(pdfFile.toPath());
            } catch (IOException e) {
                throw new PdfProcessingException("Falha ao ler bytes do arquivo PDF", e);
            }

            // ====== 2. Preparar prompt e dados ======
            String prompt;
            try {
                prompt = "Analise o currículo em PDF anexado em relação à vaga abaixo e dê resultados concretos " +
                        "como um bom analisador de currículos de qualquer área. Se atente aos detalhes " +
                        "e faça suposições de habilidades óbvias que o aplicante pode ter com base nas informações dadas " +
                        "para dar os matchs na vaga." +
                        "Vaga: ";
            } catch (Exception e) {
                throw new AiProcessingException("Erro ao montar prompt de análise", e);
            }

            // ====== 3. Serializar vaga ======
            String vacancyJson;
            try {
                vacancyJson = objectMapper.writeValueAsString(vacancy);
            } catch (Exception e) {
                throw new AiProcessingException("Erro ao converter vaga em JSON", e);
            }

            // ====== 4. Criar partes da requisição ======
            Part pdfPart = Part.fromBytes(fileBytes, "application/pdf");
            Part promptPart = Part.fromText(prompt);
            Part vacancyPart = Part.fromText(vacancyJson);

            Content content = Content.builder()
                    .role("user")
                    .parts(List.of(pdfPart, promptPart, vacancyPart))
                    .build();

            // ====== 5. Chamada à IA ======
            GenerateContentResponse response;
            try {
                response = client.models.generateContent(
                        "gemini-2.5-flash",
                        List.of(content),
                        GenerateContentConfig.builder()
                                .responseMimeType("application/json")
                                .responseSchema(AiSchema.SCHEMA)
                                .build()
                );
            } catch (Exception e) {
                throw new AiProcessingException("Falha ao gerar resposta da IA", e);
            }

            // ====== 6. Deserializar resposta ======
            AiResponseDto dto;
            try {
                dto = objectMapper.readValue(response.text(), AiResponseDto.class);
            } catch (Exception e) {
                throw new AiProcessingException("Erro ao interpretar resposta da IA (JSON inválido ou incompleto)", e);
            }

            // ====== 7. Converter para entidade ======
            return AIEntity.builder()
                    .userVacancy(userVacancyEntity)
                    .application_description(dto.getApplication_description())
                    .improvements_to_this_vacancy(dto.getImprovements_to_this_vacancy())
                    .essential_matches(dto.getEssential_matches())
                    .differential_matches(dto.getDifferential_matches())
                    .status(dto.getStatus())
                    .build();

        } catch (PdfProcessingException | AiProcessingException e) {
            System.err.println("Erro ao processar arquivo com IA: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao processar arquivo com IA: " + e.getMessage(), e);

        } catch (Exception e) {
            System.err.println("Erro inesperado no processamento de IA: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro inesperado no processamento de IA", e);
        }
    }


    public AIEntity getOneById(Long AiResponseId, Authentication authentication) {

        // ==== 1. Autenticação ====
        Optional<UserEntity> userOpt = userService.getUser(authentication);
        if (userOpt.isEmpty()) {
            throw new UnauthenticatedUser("Usuário não autenticado");
        }
        UserEntity user = userOpt.get();

        Optional<AIEntity> AiResponse = aiRepository.findById(AiResponseId);

        // verifica se existe a analise da ia
        if (AiResponse.isEmpty()) {
            throw new NotFoundException("Analise da IA não existe");
        }

        Optional<UserVacancyEntity> userVacancyOpt = userVacancyService.findById(AiResponse.get().getUserVacancy().getId());

        //verifica se existe a relação user vacancy da IA entity
        if (userVacancyOpt.isEmpty()) {
            throw new NotFoundException("Analise da IA pra essa vaga não existe mais");
        }

        Optional<List<UserVacancyEntity>> userVacancyRelationOpt = userVacancyService.findByUserAndVacancy(user, userVacancyOpt.get().getVacancy());

        if (userVacancyRelationOpt.isEmpty()) {
            throw new UserCannotAccessOrDoThat("Você não tem relação com essa vaga");
        }

        userVacancyRelationOpt.get().forEach(userVacancyEntity -> {
            if (!(user.getId().equals(userVacancyOpt.get().getUser().getId()) || userVacancyEntity.getRole().equals(VacancyRole.RECRUITER))) {
                throw new UserCannotAccessOrDoThat("Você não tem autorização para ver isso");
            }
        });
        
        return AiResponse.get();

    }

    public String melhoriasCurriculo(MultipartFile pdfFile) {
        try {
            // Converte o PDF para Base64
            byte[] fileBytes = pdfFile.getBytes();

            String prompt = " de dicas de melhorias para esse curriculo ";

            // Cria a parte do PDF inline
            Part pdfPart = Part.fromBytes(fileBytes, "application/pdf");

            // Cria a parte do prompt
            Part prompPart = Part
                    .fromText(prompt);

            // Cria o conteúdo com ambas as partes
            Content content = Content.builder()
                    .role("user")
                    .parts(List.of(pdfPart, prompPart))
                    .build();


            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    content,
                    GenerateContentConfig.builder()
                            .responseMimeType("text/plain")
                            .build()
            );

            return response.text();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo PDF", e);
        }


    }


}