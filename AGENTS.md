# \# DracoFocus Agent Rules

# 

# \## 🔒 Estado estable (NO romper)

# \- Login Google

# \- Login email

# \- Register email

# \- Laravel Sanctum

# \- Sync por lesson\_slug

# \- Room progress (Android)

# \- UI de lecciones (estados verde/completado)

# 

# \---

# 

# \## ⚠️ Reglas obligatorias

# 

# El agente DEBE seguir SIEMPRE este flujo:

# 

# 1\. Diagnóstico antes de modificar código

# 2\. Explicar el problema detectado

# 3\. Listar archivos a modificar

# 4\. Esperar confirmación del usuario

# 5\. Aplicar cambios mínimos

# 6\. Mostrar diff de archivos modificados

# 7\. Explicar cómo probar los cambios

# 

# \---

# 

# \## 🚫 Restricciones críticas

# 

# \- NO refactorizaciones grandes

# \- NO cambiar slugs existentes

# \- NO tocar auth si la tarea no es auth

# \- NO tocar progress si la tarea no es progress

# \- NO inventar endpoints sin justificar

# \- NO romper sincronización Android ↔ Laravel

# \- NO modificar estructura DB sin migración explícita

# 

# \---

# 

# \## 🧠 Reglas de seguridad

# 

# \- Si un cambio puede afectar login, sync o progreso → detenerse y preguntar

# \- Si hay duda → pedir confirmación

# \- Si hay riesgo → proponer alternativa segura

# 

# \---

# 

# \## 🧩 Stack

# 

# \- Android: Kotlin + MVVM + Room + Retrofit

# \- Backend: Laravel API REST + Sanctum

# \- Web: Laravel Blade

# \- DB: MySQL

# 

# \---

# 

# \## ⚙️ Uso obligatorio de reglas (simulación de skills)

# 

# El agente debe aplicar SIEMPRE:

# 

# \- dracofocus-safe-change → antes de cualquier modificación

# \- laravel-api-debug → cuando toque backend

# \- android-room-sync → cuando toque Android

# 

# \---

# 

# \## 🧠 Comportamiento por defecto

# 

# Si el usuario no especifica:

# 

# \- Diagnóstico primero

# \- Cambios mínimos

# \- Mantener compatibilidad Web + Android

# \- Mantener sincronización por lesson\_slug

# \- No romper flujo existente

