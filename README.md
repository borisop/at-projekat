# Agentske tehnologije - projekat

Aplikacija za prikupljanje podataka sa web site-ova bazirana na default-nom zadatku 2 na kursu "Agentske tehnologije", Fakultet tehničkih nauka, Novi Sad, 2020.

Projekat sadrži implementirane sledeće funkcionalnosti:
 - U okviru jednog čvora:
   - Startovanje agenta
   - Zaustavljanje agenta
   - Prikazivanje liste pokrenutih agenata uz upotrebu web socketa
   - Slanje ACL poruka
 - U okviru klastera:
   - Registrovanje novog čvora 
   - Dodavanje novog čvora (master dostavlja novi slave čvor ostalim slave čvorovima)
   - Uklanjanje čvora (master javlja slave čvorovima da uklone čvor)
   - Gašenje čvora
   - Dostavljanje liste pokrenutih agenata

Projekat sadrži implementirane ping i pong agente. Takođe sadrži i sledeće agente:
 - Master
 - Pretraživač
 - Sakupljač
  
Defaultni zadatak 2 nije u potpunosti implementiran. Trenutno se samo pretražuju scrap-uju web stranice i tamo gde se ključna reč poklapa čuva se tekst stranice na disku host mašine agenta.
