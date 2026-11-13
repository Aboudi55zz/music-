package com.example.data

import com.example.data.NoteCatalog.A4
import com.example.data.NoteCatalog.B_FLAT_4
import com.example.data.NoteCatalog.B_HALF_FLAT_4
import com.example.data.NoteCatalog.C4
import com.example.data.NoteCatalog.C5
import com.example.data.NoteCatalog.D4
import com.example.data.NoteCatalog.E4
import com.example.data.NoteCatalog.E_FLAT_4
import com.example.data.NoteCatalog.E_HALF_FLAT_4
import com.example.data.NoteCatalog.F4
import com.example.data.NoteCatalog.F_SHARP_4
import com.example.data.NoteCatalog.G4
import com.example.data.NoteCatalog.G_FLAT_4
import com.example.model.TheoryTopic

object TheoryRepository {
    val topics: List<TheoryTopic> = listOf(
        TheoryTopic(
            id = "quarter_tone",
            title = "سر الربع تون (نصف البيمول 𝄳)",
            subtitle = "كيف ينقسم الديوان العربي إلى ٢٤ ربع صوت؟",
            body = "في الموسيقى الغربية ينقسم الأوكتاف (الديوان) إلى ١٢ نصف تون متساوية، أي أن المسافة الكاملة (مثل دو ← ري) تساوي نصفين (٢ من أنصاف التون). أما في الموسيقى العربية المشرقية فينقسم الديوان إلى ٢٤ ربع تون! وهذا يعني أن كل بعد كامل يساوي ٤ أرباع تون، والبعد المتوسط (ثلاثة أرباع التون) يقع في المنتصف تماماً ويُرمز له بعلامة نصف البيمول (𝄳) التي تسمى «سيكاه».",
            keyPoints = listOf(
                "البعد الكامل = ٤ أرباع تون (مثل: دو ← ري)",
                "ثلاثة أرباع التون = ٣ أرباع تون (مثل: ري ← مي نصف بيمول)",
                "نصف البعد = ٢ ربع تون (مثل: دو ← دو دييز)",
                "البعد الزائد = ٦ أرباع تون (مثل: مي بيمول ← فا دييز في الحجاز)"
            ),
            demoTitle = "اسمع الفرق: مي طبيعي ← مي نصف بيمول ← مي بيمول",
            demoNotes = listOf(
                E4.withDuration(1.2f),
                E_HALF_FLAT_4.withDuration(1.2f),
                E_FLAT_4.withDuration(1.2f),
                E_HALF_FLAT_4.withDuration(1.2f),
                E4.withDuration(1.5f)
            )
        ),
        TheoryTopic(
            id = "ajnas",
            title = "الأجناس الموسيقية: لبنات بناء المقام",
            subtitle = "كيف يتكوّن المقام من جنس الأصل وجنس الفرع؟",
            body = "لا يُبنى المقام العربي كقائمة جامدة من النغمات، بل يتألف من تزاوج وحدتين صغيرتين تُسمى كل منهما «جِنس» (الجمع: أجناس). يتكون الجنس غالباً من ٣ أو ٤ أو ٥ نغمات متتالية تحدد الطابع العاطفي. يتحد «جنس الأصل» في الأسفل مع «جنس الفرع» في الأعلى عند نقطة التقاء تُدعى «الغمّاز».",
            keyPoints = listOf(
                "جنس الراست (دو-ري-مي𝄳-فا-صول): أبعاد ٤ - ٣ - ٣ أرباع",
                "جنس البياتي (ري-مي𝄳-فا-صول): أبعاد ٣ - ٣ - ٤ أرباع",
                "جنس الحجاز (ري-مي♭-فا♯-صول): أبعاد ٢ - ٦ - ٢ أرباع",
                "جنس الكرد (ري-مي♭-فا-صول): أبعاد ٢ - ٤ - ٤ أرباع",
                "جنس الصبا (ري-مي𝄳-فا-صول♭): أبعاد ٣ - ٣ - ٢ أرباع"
            ),
            demoTitle = "مثال تطبيقي: جنس الحجاز على الري",
            demoNotes = listOf(
                D4.withDuration(1f),
                E_FLAT_4.withDuration(1f),
                F_SHARP_4.withDuration(1f),
                G4.withDuration(1.5f)
            )
        ),
        TheoryTopic(
            id = "arabic_degrees",
            title = "الأسماء التراثية لدرجات السلم العربي",
            subtitle = "من يكاه (صول قرار) إلى كردان (دو جواب)",
            body = "ابتكر الموسيقيون العرب والفرس والأتراك أسماء شعرية خاصة لكل درجة على سلم الراست الأساسي، تُستخدم في التدوين التراثي والموشحات لتمييز الطبقة الصوتية بدقة، ولتسمية المقامات أحياناً باسم الدرجة التي ترتكز عليها (مثل مقام السيكاه والعراق والجهاركاه).",
            keyPoints = listOf(
                "صول قرار = يَكاه | لا قرار = عُشيران | سي𝄳 قرار = عِراق",
                "دو = راست | ري = دوكاه | مي𝄳 = سيكاه | فا = جهاركاه",
                "صول = نوى | لا = حسيني | سي𝄳 = أوج | دو جواب = كردان"
            ),
            demoTitle = "عزف سلم الدرجات التراثية من الدو إلى الكردان",
            demoNotes = listOf(C4, D4, E_HALF_FLAT_4, F4, G4, A4, B_HALF_FLAT_4, C5)
        ),
        TheoryTopic(
            id = "sayr_and_jam",
            title = "السير، الركوز، الغمّاز والجمع",
            subtitle = "الروح الحية التي تميّز مقاماً عن آخر",
            body = "قد يشترك مقامان في نفس النغمات تماماً (مثل البياتي والحسيني، أو الراست والماهور تقريباً)، لكن ما يفرّق بينهما هو «سير المقام» أي مسار الجملة اللحنية ونقاط التوقف. «درجة الركوز» هي البيت الذي يرتاح عنده اللحن في النهاية، بينما «الغمّاز» هو الجسر الذي ينتقل منه العازف إلى جنس الفرع.",
            keyPoints = listOf(
                "الجمع المتصل: تبدأ نغمة جنس الفرع من نفس النغمة الأخيرة لجنس الأصل (مثل البياتي).",
                "الجمع المنفصل: يفصل بين الجنسين مسافة بُعد كامل (مثل الراست والنهوند).",
                "الجمع المتداخل: يبدأ جنس الفرع قبل نهاية جنس الأصل بنغمة (مثل الصبا والسيكاه)."
            ),
            demoTitle = "نموذج استقرار جنس الصبا وتداخله مع الحجاز",
            demoNotes = listOf(
                D4.withDuration(1f),
                E_HALF_FLAT_4.withDuration(0.8f),
                F4.withDuration(1f),
                G_FLAT_4.withDuration(1.2f),
                A4.withDuration(1f),
                B_FLAT_4.withDuration(1.5f)
            )
        )
    )
}
