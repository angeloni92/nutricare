package com.angeloni.nutricare.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.entity.AnthropometryEntity;
import com.angeloni.nutricare.entity.ClientEntity;
import com.angeloni.nutricare.entity.DietResultEntity;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.repository.AnthropometryRepository;
import com.angeloni.nutricare.repository.ClientRepository;
import com.angeloni.nutricare.repository.DietResultRepository;
import com.angeloni.nutricare.service.UserContextService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(10)
public class DemoDataInitializer {

    @Autowired
    private UserContextService userContextService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AnthropometryRepository anthropometryRepository;

    @Autowired
    private DietResultRepository dietResultRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initDemoData() {
        UserEntity user = userContextService.getCurrentUser();
        if (user == null || !clientRepository.findByUser(user).isEmpty()) {
            log.info("Demo data already present, skipping initialization");
            return;
        }

        List<ClientEntity> clients = createClients(user);
        createAnthropometries(clients);
        createDietResults(user, clients);
        log.info("Demo data initialized: {} clients created", clients.size());
    }

    private List<ClientEntity> createClients(UserEntity user) {
        ClientEntity marco = clientRepository.save(ClientEntity.builder()
                .user(user).name("Marco").surname("Rossi").age(35).country("Italia")
                .allergies(new ArrayList<>(List.of("Nichel")))
                .healthConditions(new ArrayList<>(List.of("Sovrappeso")))
                .build());

        ClientEntity giulia = clientRepository.save(ClientEntity.builder()
                .user(user).name("Giulia").surname("Bianchi").age(28).country("Italia")
                .allergies(new ArrayList<>())
                .healthConditions(new ArrayList<>())
                .build());

        ClientEntity luca = clientRepository.save(ClientEntity.builder()
                .user(user).name("Luca").surname("Ferrari").age(52).country("Italia")
                .allergies(new ArrayList<>(List.of("Crostacei")))
                .healthConditions(new ArrayList<>(List.of("Ipertensione", "Colesterolo alto")))
                .build());

        ClientEntity sofia = clientRepository.save(ClientEntity.builder()
                .user(user).name("Sofia").surname("Russo").age(41).country("Italia")
                .allergies(new ArrayList<>(List.of("Glutine")))
                .healthConditions(new ArrayList<>(List.of("Celiachia")))
                .build());

        ClientEntity matteo = clientRepository.save(ClientEntity.builder()
                .user(user).name("Matteo").surname("Conti").age(23).country("Italia")
                .allergies(new ArrayList<>())
                .healthConditions(new ArrayList<>())
                .build());

        return List.of(marco, giulia, luca, sofia, matteo);
    }

    private void createAnthropometries(List<ClientEntity> clients) {
        anthropometryRepository.save(AnthropometryEntity.builder()
                .client(clients.get(0)).height(178.0).weight(95.5).build());
        anthropometryRepository.save(AnthropometryEntity.builder()
                .client(clients.get(1)).height(165.0).weight(58.0).build());
        anthropometryRepository.save(AnthropometryEntity.builder()
                .client(clients.get(2)).height(172.0).weight(85.0).build());
        anthropometryRepository.save(AnthropometryEntity.builder()
                .client(clients.get(3)).height(163.0).weight(62.0).build());
        anthropometryRepository.save(AnthropometryEntity.builder()
                .client(clients.get(4)).height(183.0).weight(78.0).build());
    }

    private void createDietResults(UserEntity user, List<ClientEntity> clients) {
        dietResultRepository.save(DietResultEntity.builder()
                .user(user)
                .clientId(clients.get(0).getId())
                .aiModel("CLAUDE5SONNET")
                .generatedDiet(dietMarcoRossi())
                .build());

        dietResultRepository.save(DietResultEntity.builder()
                .user(user)
                .clientId(clients.get(4).getId())
                .aiModel("GPT4O")
                .generatedDiet(dietMatteoConti())
                .build());
    }

    private String dietMarcoRossi() {
        return """
                PIANO NUTRIZIONALE SETTIMANALE — Marco Rossi
                Obiettivo: Perdita di peso | Calorie target: 1.800 kcal/giorno

                ═══════════════════════════════════════════════════
                LUNEDÌ
                ───────────────────────────────────────────────────
                Colazione (350 kcal)
                • Yogurt greco 0% (150 g) con muesli senza zucchero (40 g)
                • 1 mela
                • Tè verde senza zucchero

                Spuntino mattina (100 kcal)
                • 15 mandorle non salate

                Pranzo (550 kcal)
                • Pasta integrale (70 g a crudo) con pomodoro fresco e basilico
                • Insalata mista con 1 cucchiaio di olio EVO

                Spuntino pomeriggio (100 kcal)
                • 1 centrifugato di verdure (sedano, carota, zenzero)

                Cena (700 kcal)
                • Petto di pollo alla griglia (180 g) con erbe aromatiche
                • Verdure al vapore (zucchine e broccoli, 300 g)
                • 1 fetta di pane integrale (30 g)
                • 1 cucchiaio di olio EVO

                Totale stima: ~1.800 kcal

                ═══════════════════════════════════════════════════
                MARTEDÌ
                ───────────────────────────────────────────────────
                Colazione (350 kcal)
                • 2 uova strapazzate con spinaci
                • 1 fetta di pane integrale tostata
                • Caffè senza zucchero

                Spuntino mattina (100 kcal)
                • 1 arancia

                Pranzo (550 kcal)
                • Zuppa di legumi (ceci e lenticchie, 300 g)
                • Insalata di finocchi
                • 1 cucchiaio di olio EVO

                Spuntino pomeriggio (100 kcal)
                • 1 vasetto di yogurt bianco magro

                Cena (700 kcal)
                • Salmone al forno con limone e rosmarino (150 g)
                • Patate lesse (100 g)
                • Cavolfiore al vapore con curcuma
                • 1 cucchiaio di olio EVO

                Totale stima: ~1.800 kcal

                ═══════════════════════════════════════════════════
                MERCOLEDÌ
                ───────────────────────────────────────────────────
                Colazione (350 kcal)
                • Porridge di fiocchi d'avena (50 g) con latte scremato e frutti di bosco
                • Tè verde

                Spuntino mattina (100 kcal)
                • 10 noci

                Pranzo (550 kcal)
                • Riso integrale (70 g a crudo) con verdure saltate e tofu (80 g)
                • Radicchio con aceto di mele

                Spuntino pomeriggio (100 kcal)
                • 1 pera

                Cena (700 kcal)
                • Filetto di merluzzo al vapore (180 g)
                • Spinaci saltati con aglio e olio
                • Quinoa (60 g a crudo)

                Totale stima: ~1.800 kcal

                ═══════════════════════════════════════════════════
                GIOVEDÌ — VENERDÌ — SABATO — DOMENICA
                ───────────────────────────────────────────────────
                Seguire rotazione simile ai giorni precedenti, variando le proteine
                (tacchino, sgombro, uova, legumi) e le verdure di stagione.
                Il sabato è consentito un pasto libero (max 2.200 kcal totali giornaliere).

                ═══════════════════════════════════════════════════
                CONSIGLI PRATICI
                ───────────────────────────────────────────────────
                ✓ Bere almeno 2 litri di acqua al giorno
                ✓ Camminare 30 minuti al giorno come obiettivo minimo
                ✓ Evitare bevande zuccherate e alcolici nei giorni feriali
                ✓ Cucinare preferibilmente al vapore, alla griglia o al forno
                ✓ Olio EVO: max 3 cucchiai al giorno

                Piano generato da NutriCare — Revisione medica raccomandata prima di iniziare
                """;
    }

    private String dietMatteoConti() {
        return """
                PIANO NUTRIZIONALE SETTIMANALE — Matteo Conti
                Obiettivo: Performance atletica e aumento massa muscolare
                Calorie target: 3.200 kcal (giorni di allenamento) | 2.600 kcal (riposo)

                ═══════════════════════════════════════════════════
                LUNEDÌ (Allenamento mattina)
                ───────────────────────────────────────────────────
                Pre-allenamento ore 6:30 (200 kcal)
                • 1 banana + 30 g di riso soffiato

                Colazione post-allenamento ore 8:30 (700 kcal)
                • 3 uova strapazzate con avena (60 g) e latte intero
                • 200 g di ricotta con miele e noci
                • Succo di arancia fresco (300 ml)

                Spuntino mattina (350 kcal)
                • Frullato: latte intero 300 ml + 1 banana + 30 g burro di arachidi

                Pranzo (900 kcal)
                • Pasta (120 g a crudo) con ragù di manzo magro (150 g)
                • Insalata mista con 2 cucchiai di olio EVO
                • Pane integrale (60 g)

                Spuntino pomeriggio (350 kcal)
                • Yogurt greco 2% (200 g) con granola e frutti di bosco (100 g)

                Cena (700 kcal)
                • Petto di pollo alla piastra (220 g) con erbe
                • Riso basmati (80 g a crudo) con verdure grigliate
                • 2 cucchiai di olio EVO

                Totale stima: ~3.200 kcal

                ═══════════════════════════════════════════════════
                MARTEDÌ (Riposo / recupero)
                ───────────────────────────────────────────────────
                Calorie ridotte a ~2.600 kcal — porzioni di carboidrati ridotte del 30%

                Colazione (500 kcal)
                • Fiocchi d'avena (80 g) con latte, banana e miele
                • 2 uova sode

                Pranzo (750 kcal)
                • Riso integrale (100 g a crudo) con salmone (150 g) e verdure

                Cena (650 kcal)
                • Manzo magro (200 g) con patate al forno (150 g) e broccoli

                ═══════════════════════════════════════════════════
                MERCOLEDÌ — GIOVEDÌ — VENERDÌ — SABATO — DOMENICA
                ───────────────────────────────────────────────────
                • Giorni allenamento (mer, ven, sab): schema lunedì (~3.200 kcal)
                • Giorni riposo (gio, dom): schema martedì (~2.600 kcal)

                ═══════════════════════════════════════════════════
                CONSIGLI PRATICI PER L'ATLETA
                ───────────────────────────────────────────────────
                ✓ Idratazione: 3-4 litri/giorno, +500 ml per ogni ora di allenamento
                ✓ Timing: carboidrati complessi 2h prima dell'allenamento
                ✓ Recupero: proteine entro 30 min dopo l'allenamento
                ✓ Sonno: 8-9 ore per ottimizzare la sintesi proteica
                ✓ Monitorare il peso ogni lunedì mattina a digiuno

                Piano generato da NutriCare — Revisione medica raccomandata prima di iniziare
                """;
    }
}
