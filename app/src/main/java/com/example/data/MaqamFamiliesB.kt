package com.example.data

import com.example.data.NoteCatalog.A3
import com.example.data.NoteCatalog.A4
import com.example.data.NoteCatalog.A_FLAT_4
import com.example.data.NoteCatalog.A_HALF_FLAT_4
import com.example.data.NoteCatalog.B3
import com.example.data.NoteCatalog.B4
import com.example.data.NoteCatalog.B_FLAT_3
import com.example.data.NoteCatalog.B_FLAT_4
import com.example.data.NoteCatalog.B_HALF_FLAT_3
import com.example.data.NoteCatalog.B_HALF_FLAT_4
import com.example.data.NoteCatalog.C4
import com.example.data.NoteCatalog.C5
import com.example.data.NoteCatalog.D4
import com.example.data.NoteCatalog.D5
import com.example.data.NoteCatalog.D_FLAT_4
import com.example.data.NoteCatalog.D_FLAT_5
import com.example.data.NoteCatalog.E4
import com.example.data.NoteCatalog.E5
import com.example.data.NoteCatalog.E_FLAT_4
import com.example.data.NoteCatalog.E_FLAT_5
import com.example.data.NoteCatalog.E_HALF_FLAT_4
import com.example.data.NoteCatalog.E_HALF_FLAT_5
import com.example.data.NoteCatalog.F4
import com.example.data.NoteCatalog.F5
import com.example.data.NoteCatalog.F_SHARP_4
import com.example.data.NoteCatalog.G3
import com.example.data.NoteCatalog.G4
import com.example.data.NoteCatalog.G5
import com.example.data.NoteCatalog.G_FLAT_4
import com.example.model.FamousExample
import com.example.model.JinsInfo
import com.example.model.Maqam
import com.example.model.MaqamFamily

internal object MaqamFamiliesB {
    val kurdNahawandAjamSabaSikahNikriz: List<Maqam> = listOf(
        // ==================== 4. KURD FAMILY ====================
        Maqam(
            id = "kurd",
            name = "مقام الكرد",
            englishName = "Maqam Kurd",
            family = MaqamFamily.KURD,
            hasQuarterTone = false,
            tonicName = "ري (دوكاه)",
            ghammazName = "صول (نوى)",
            jamType = "جمع متصل",
            moodTags = listOf("رومانسي", "ناعم", "عاطفي", "عصري"),
            summary = "مقام رقيق يخلو من أرباع التون؛ يبدأ بنصف بعد بين الري والمي بيمول مما يجعله قريباً من الموسيقى المعاصرة.",
            detailedExplanation = "يعبّر مقام الكرد عن الحنان والرقة العاطفية، ويُعد الأكثر استخداماً في الأغاني العربية الحديثة والرومانسية لخلوه من الربع تون وسهولة اندماجه مع الهارموني الغربي.",
            sayrExplanation = "ينطلق من الري ويؤكد على المي بيمول كعلامة مميزة، ثم يرتكز على الصول (نهاوند أو كرد على الصول) قبل العودة السلسة إلى الدوكاه.",
            ascendingScale = listOf(D4, E_FLAT_4, F4, G4, A4, B_FLAT_4, C5, D5),
            descendingScale = listOf(D5, C5, B_FLAT_4, A4, G4, F4, E_FLAT_4, D4),
            melodyTitle = "عبارة كردية رومانسية رقيقة",
            melodyPhrase = listOf(
                D4.withDuration(1f), E_FLAT_4.withDuration(0.5f), F4.withDuration(0.5f),
                G4.withDuration(1.5f), F4.withDuration(0.5f), E_FLAT_4.withDuration(0.5f),
                F4.withDuration(0.5f), G4.withDuration(1f), A4.withDuration(0.5f),
                B_FLAT_4.withDuration(1f), A4.withDuration(0.5f), G4.withDuration(1f),
                F4.withDuration(0.5f), E_FLAT_4.withDuration(0.5f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الكرد", "ري (دوكاه)", "2 - 4 - 4", "½ - 1 - 1", listOf(D4, E_FLAT_4, F4, G4), "بداية بنصف بعد تمنح رقة وعذوبة."),
            branchJins = JinsInfo("جنس النهاوند على النوى", "صول (نوى)", "4 - 2 - 4", "1 - ½ - 1", listOf(G4, A4, B_FLAT_4, C5, D5), "امتداد متجانس حتى جواب الري."),
            famousExamples = listOf(
                FamousExample("لسه فاكر", "أم كلثوم", "طرب كلاسيكي"),
                FamousExample("نسم علينا الهوى", "فيروز", "أغنية رحبانية"),
                FamousExample("تخونوه", "عبد الحليم حافظ", "كلاسيكي")
            )
        ),
        Maqam(
            id = "hijaz_kar_kurd",
            name = "مقام حجاز كار كرد",
            englishName = "Maqam Hijaz Kar Kurd",
            family = MaqamFamily.KURD,
            hasQuarterTone = false,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("متأمل", "حالم", "رشيق"),
            summary = "تصوير الكرد على درجة الدو مع تلوين جنس النهاوند أو الكرد على الصول.",
            detailedExplanation = "يشبه في هيكله مقام الكرد مصوراً على الدو، ويُسمى أيضاً «كوردي على الراست». يتميز بسلاسة بالغة في الإيقاع واللحن مع الحفاظ على الرومانسية الحالمة.",
            sayrExplanation = "يتجول في جنس الكرد بين الدو والفا، ثم ينتقل إلى الصول ليعزف جنس الكرد أو النهاوند، وكثيراً ما يلمس السي الطبيعية عند القرار.",
            ascendingScale = listOf(C4, D_FLAT_4, E_FLAT_4, F4, G4, A_FLAT_4, B_FLAT_4, C5),
            descendingScale = listOf(C5, B_FLAT_4, A_FLAT_4, G4, F4, E_FLAT_4, D_FLAT_4, C4),
            melodyTitle = "لحن حالم في حجاز كار كرد",
            melodyPhrase = listOf(
                C4.withDuration(1f), D_FLAT_4.withDuration(0.5f), E_FLAT_4.withDuration(1f),
                F4.withDuration(0.5f), G4.withDuration(1.5f), A_FLAT_4.withDuration(0.5f),
                B_FLAT_4.withDuration(0.5f), C5.withDuration(1f), B_FLAT_4.withDuration(0.5f),
                A_FLAT_4.withDuration(0.5f), G4.withDuration(1f), F4.withDuration(0.5f),
                E_FLAT_4.withDuration(0.5f), D_FLAT_4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الكرد", "دو", "2 - 4 - 4", "½ - 1 - 1", listOf(C4, D_FLAT_4, E_FLAT_4, F4), "كرد مصور على درجة الدو."),
            branchJins = JinsInfo("جنس الكرد على النوى", "صول", "2 - 4 - 4", "½ - 1 - 1", listOf(G4, A_FLAT_4, B_FLAT_4, C5), "تناظر تام مع جنس الأصل."),
            famousExamples = listOf(
                FamousExample("يا زهرة في خيالي", "فريد الأطرش", "أغنية رومانسية"),
                FamousExample("سهر الليالي", "فيروز", "رحبانيات")
            )
        ),
        Maqam(
            id = "lami",
            name = "مقام اللامي",
            englishName = "Maqam Lami",
            family = MaqamFamily.KURD,
            hasQuarterTone = false,
            tonicName = "ري (دوكاه)",
            ghammazName = "صول (نوى)",
            jamType = "جمع متصل",
            moodTags = listOf("عراقي", "شجي", "حنين"),
            summary = "مقام عراقي أصيل يتميز بنزول الدرجة الخامسة إلى لا بيمول، مما يعطيه شجناً فريداً.",
            detailedExplanation = "أحد أركان المقام العراقي التراثي. يشبه الكرد في بدايته (ري، مي بيمول، فا، صول) غير أنه يخفّض درجة اللا إلى لا بيمول، فيخلق مسافة جديدة مليئة بالحنين.",
            sayrExplanation = "يبرز لا بيمول فوق الغمّاز صول مباشرة، ثم يعود للقرار ري بهدوء ووقار.",
            ascendingScale = listOf(D4, E_FLAT_4, F4, G4, A_FLAT_4, B_FLAT_4, C5, D5),
            descendingScale = listOf(D5, C5, B_FLAT_4, A_FLAT_4, G4, F4, E_FLAT_4, D4),
            melodyTitle = "شجن اللامي البغدادي",
            melodyPhrase = listOf(
                D4.withDuration(1f), E_FLAT_4.withDuration(0.5f), F4.withDuration(0.5f),
                G4.withDuration(1f), A_FLAT_4.withDuration(1f), B_FLAT_4.withDuration(0.5f),
                C5.withDuration(0.5f), D5.withDuration(1f), C5.withDuration(0.5f),
                B_FLAT_4.withDuration(0.5f), A_FLAT_4.withDuration(1f), G4.withDuration(0.5f),
                F4.withDuration(0.5f), E_FLAT_4.withDuration(0.5f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الكرد", "ري (دوكاه)", "2 - 4 - 4", "½ - 1 - 1", listOf(D4, E_FLAT_4, F4, G4), "قاعدة الكرد الصافية."),
            branchJins = JinsInfo("جنس الكرد على النوى", "صول (نوى)", "2 - 4 - 4", "½ - 1 - 1", listOf(G4, A_FLAT_4, B_FLAT_4, C5), "لا بيمول هي العلامة الفارقة للامي."),
            famousExamples = listOf(
                FamousExample("يا أم العيون السود", "ناظم الغزالي", "تراث عراقي"),
                FamousExample("مرّوا عليّ الحلوين", "تراث عراقي", "مقام لامي")
            )
        ),

        // ==================== 5. NAHAWAND FAMILY ====================
        Maqam(
            id = "nahawand",
            name = "مقام النهوند",
            englishName = "Maqam Nahawand",
            family = MaqamFamily.NAHAWAND,
            hasQuarterTone = false,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("درامي", "حالم", "سردي", "عاطفي"),
            summary = "شبيه بالسلم الصغير الغربي؛ يجمع بين الشاعرية والعمق الدرامي دون استخدام أرباع التون.",
            detailedExplanation = "يعد النهوند جسراً مثالياً بين الموسيقى العربية والعالمية. يبدأ بجنس النهاوند الخماسي على الدو (دو، ري، مي بيمول، فا، صول) ويتبعه جنس الحجاز على الصول في الصعود أو جنس الكرد في الهبوط.",
            sayrExplanation = "يتمركز في الدو والصول، ويستخدم السي الطبيعية كحسّاس للعودة القوية نحو الدو، بينما يستخدم السي بيمول في الانسياب الهابط.",
            ascendingScale = listOf(C4, D4, E_FLAT_4, F4, G4, A_FLAT_4, B4, C5),
            descendingScale = listOf(C5, B_FLAT_4, A_FLAT_4, G4, F4, E_FLAT_4, D4, C4),
            melodyTitle = "جملة نهوند درامية مع الحساس",
            melodyPhrase = listOf(
                C4.withDuration(1f), D4.withDuration(0.5f), E_FLAT_4.withDuration(1f),
                F4.withDuration(0.5f), G4.withDuration(1.5f), A_FLAT_4.withDuration(0.5f),
                B4.withDuration(0.5f), C5.withDuration(1.5f), B_FLAT_4.withDuration(0.5f),
                A_FLAT_4.withDuration(0.5f), G4.withDuration(1f), F4.withDuration(0.5f),
                E_FLAT_4.withDuration(0.5f), D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس النهاوند", "دو (راست)", "4 - 2 - 4 - 4", "1 - ½ - 1 - 1", listOf(C4, D4, E_FLAT_4, F4, G4), "أساس عاطفي رصين وعميق."),
            branchJins = JinsInfo("جنس الحجاز على النوى", "صول (نوى)", "2 - 6 - 2", "½ - 1½ - ½", listOf(G4, A_FLAT_4, B4, C5), "يتحول إلى كرد على الصول (سي بيمول) في الهبوط."),
            famousExamples = listOf(
                FamousExample("ألف ليلة وليلة", "أم كلثوم", "مقدمة وبداية"),
                FamousExample("دارت الأيام", "أم كلثوم", "طرب"),
                FamousExample("أنا لك على طول", "عبد الحليم حافظ", "رومانسي")
            )
        ),
        Maqam(
            id = "farahfaza",
            name = "مقام فرحفزا",
            englishName = "Maqam Farahfaza",
            family = MaqamFamily.NAHAWAND,
            hasQuarterTone = false,
            tonicName = "صول قرار (يكاه)",
            ghammazName = "ري (دوكاه)",
            jamType = "جمع منفصل",
            moodTags = listOf("مبهج", "فخم", "رقراق"),
            summary = "نهوند مصوّر على درجة الصول القرار مع نكهة العجم المشرقة في الفرع.",
            detailedExplanation = "يعني اسمه «الباعث على الفرح والسرور». هو مقام نهوند يرتكز على درجة الصول القرار (G3) مما يعطيه فخامة صوتية خاصة جداً.",
            sayrExplanation = "ينساب بلطف في جنس النهاوند على الصول القرار، وغالباً ما يختم بجنس العجم على السي بيمول قبل القفلة.",
            ascendingScale = listOf(G3, A3, B_FLAT_3, C4, D4, E_FLAT_4, F_SHARP_4, G4),
            descendingScale = listOf(G4, F4, E_FLAT_4, D4, C4, B_FLAT_3, A3, G3),
            melodyTitle = "بهجة الفرحفزا على الصول قرار",
            melodyPhrase = listOf(
                G3.withDuration(1f), A3.withDuration(0.5f), B_FLAT_3.withDuration(1f),
                C4.withDuration(0.5f), D4.withDuration(1.5f), E_FLAT_4.withDuration(0.5f),
                F_SHARP_4.withDuration(0.5f), G4.withDuration(1f), F4.withDuration(0.5f),
                E_FLAT_4.withDuration(0.5f), D4.withDuration(1f), C4.withDuration(0.5f),
                B_FLAT_3.withDuration(0.5f), A3.withDuration(0.5f), G3.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس النهاوند", "صول قرار (يكاه)", "4 - 2 - 4", "1 - ½ - 1", listOf(G3, A3, B_FLAT_3, C4, D4), "نهوند دافئ في طبقة القرار."),
            branchJins = JinsInfo("جنس الحجاز / الكرد على الدوكاه", "ري (دوكاه)", "2 - 6 - 2", "½ - 1½ - ½", listOf(D4, E_FLAT_4, F_SHARP_4, G4), "تلوين رشيق في الجواب."),
            famousExamples = listOf(
                FamousExample("يا حلاوة الدنيا", "زكريا أحمد", "طرب"),
                FamousExample("عزيزة", "محمد عبد الوهاب", "مقطوعة موسيقية")
            )
        ),
        Maqam(
            id = "nawa_athar",
            name = "مقام نوا أثر",
            englishName = "Maqam Nawa Athar",
            family = MaqamFamily.NAHAWAND,
            hasQuarterTone = false,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("غامض", "درامي", "آسر"),
            summary = "يجمع جنس النكريز على الدو مع جنس الحجاز على الصول ليكون مقاماً مليئاً بالإثارة والغموض.",
            detailedExplanation = "يتميز نوا أثر بوجود درجتي فا دييز ولا بيمول معاً، مما يخلق ثنائيات زائدة درامية للغاية بدون أي ربع تون.",
            sayrExplanation = "يدور في جنس النكريز الخماسي حول الدو والفا دييز، ثم يتفرع إلى الحجاز على الصول وصولاً للجواب.",
            ascendingScale = listOf(C4, D4, E_FLAT_4, F_SHARP_4, G4, A_FLAT_4, B4, C5),
            descendingScale = listOf(C5, B4, A_FLAT_4, G4, F_SHARP_4, E_FLAT_4, D4, C4),
            melodyTitle = "إثارة النوا أثر وسحره الدرامي",
            melodyPhrase = listOf(
                C4.withDuration(1f), D4.withDuration(0.5f), E_FLAT_4.withDuration(0.5f),
                F_SHARP_4.withDuration(1f), G4.withDuration(1.5f), A_FLAT_4.withDuration(0.5f),
                B4.withDuration(1f), C5.withDuration(1f), B4.withDuration(0.5f),
                A_FLAT_4.withDuration(0.5f), G4.withDuration(1f), F_SHARP_4.withDuration(0.5f),
                E_FLAT_4.withDuration(0.5f), D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس النكريز", "دو", "4 - 2 - 6 - 2", "1 - ½ - 1½ - ½", listOf(C4, D4, E_FLAT_4, F_SHARP_4, G4), "نكهة غامضة بفضل الفا دييز."),
            branchJins = JinsInfo("جنس الحجاز على النوى", "صول", "2 - 6 - 2", "½ - 1½ - ½", listOf(G4, A_FLAT_4, B4, C5), "يكثف الإحساس الدرامي في الجواب."),
            famousExamples = listOf(
                FamousExample("فقدتك يا أعز الناس (مقطع)", "فريد الأطرش", "طرب"),
                FamousExample("موشح جفنه علم الغزل (لمسة)", "محمد عبد الوهاب", "كلاسيكي")
            )
        ),

        // ==================== 6. AJAM FAMILY ====================
        Maqam(
            id = "ajam",
            name = "مقام العجم",
            englishName = "Maqam Ajam",
            family = MaqamFamily.AJAM,
            hasQuarterTone = false,
            tonicName = "سي بيمول (أو دو)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("مشرق", "حماسي", "واضح", "فرح"),
            summary = "يناسب الأناشيد والأفراح؛ يطابق السلم الكبير (Major) في نقائه وإشراقه وقوته.",
            detailedExplanation = "مقام العجم خالٍ تماماً من أرباع التون، ويشعّ قوة وتفاؤلاً ووضوحاً. نعرضه هنا على درجة الدو (دو، ري، مي، فا، صول، لا، سي، دو) لسهولة قراءة نوتته ومقارنته مع الراست.",
            sayrExplanation = "ينطلق بقوة من القرار نحو الدرجة الثالثة (مي) ثم الخامسة (صول)، مع تلوينات خفيفة أحياناً على السي بيمول عند التحويل.",
            ascendingScale = listOf(C4, D4, E4, F4, G4, A4, B4, C5),
            descendingScale = listOf(C5, B4, A4, G4, F4, E4, D4, C4),
            melodyTitle = "إشراقة العجم الاحتفالية",
            melodyPhrase = listOf(
                C4.withDuration(1f), E4.withDuration(0.5f), G4.withDuration(1f),
                A4.withDuration(0.5f), G4.withDuration(0.5f), F4.withDuration(0.5f),
                E4.withDuration(1f), F4.withDuration(0.5f), G4.withDuration(0.5f),
                A4.withDuration(0.5f), B4.withDuration(0.5f), C5.withDuration(1.5f),
                G4.withDuration(0.5f), E4.withDuration(0.5f), D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس العجم", "دو", "4 - 4 - 2", "1 - 1 - ½", listOf(C4, D4, E4, F4, G4), "بناء واضح وقوي ومشرق."),
            branchJins = JinsInfo("جنس العجم على النوى", "صول", "4 - 4 - 2", "1 - 1 - ½", listOf(G4, A4, B4, C5), "امتداد متطابق يمنح سطوعاً بهياً."),
            famousExamples = listOf(
                FamousExample("طلعت يا محلا نورها", "سيد درويش", "أغنية شعبية"),
                FamousExample("أهواك", "عبد الحليم حافظ", "رومانسي"),
                FamousExample("زوروني كل سنة مرة", "سيد درويش", "طقطوقة")
            )
        ),
        Maqam(
            id = "jiharkah",
            name = "مقام الجهاركاه",
            englishName = "Maqam Jiharkah",
            family = MaqamFamily.AJAM,
            hasQuarterTone = true,
            tonicName = "فا (جهاركاه)",
            ghammazName = "دو جواب (كردان)",
            jamType = "جمع منفصل",
            moodTags = listOf("فرح", "شعبي", "عذب", "شرقي"),
            summary = "يرتكز على الفا بجنس عجم في البداية وجنس راست لطيف (مع مي نصف بيمول) في الجواب.",
            detailedExplanation = "يُصنف في عائلة العجم بسبب جنس أصله، لكنه يستخدم في جوابه نغمة مي نصف بيمول (في جنس الراست على الدو)، ما يكسبه طعماً عربياً شعبياً مبهجاً.",
            sayrExplanation = "يبدأ مشرقاً على الفا، ثم يرتقي إلى الدو العالي (كردان) ليُدخل نغمة السيكاه الخفيفة قبل الهبوط للفا.",
            ascendingScale = listOf(F4, G4, A4, B_FLAT_4, C5, D5, E_HALF_FLAT_5, F5),
            descendingScale = listOf(F5, E_HALF_FLAT_5, D5, C5, B_FLAT_4, A4, G4, F4),
            melodyTitle = "بهجة الجهاركاه من الفا إلى الفا جواب",
            melodyPhrase = listOf(
                F4.withDuration(1f), G4.withDuration(0.5f), A4.withDuration(1f),
                B_FLAT_4.withDuration(0.5f), C5.withDuration(1.5f), D5.withDuration(0.5f),
                E_HALF_FLAT_5.withDuration(1f), F5.withDuration(1f), E_HALF_FLAT_5.withDuration(0.5f),
                D5.withDuration(0.5f), C5.withDuration(1f), B_FLAT_4.withDuration(0.5f),
                A4.withDuration(0.5f), G4.withDuration(0.5f), F4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس العجم", "فا (جهاركاه)", "4 - 4 - 2", "1 - 1 - ½", listOf(F4, G4, A4, B_FLAT_4, C5), "إشراقة الفا الصافية."),
            branchJins = JinsInfo("جنس الراست على الكردان", "دو جواب", "4 - 3 - 3", "1 - ¾ - ¾", listOf(C5, D5, E_HALF_FLAT_5, F5), "يضيف اللمسة الشرقية المميزة."),
            famousExamples = listOf(
                FamousExample("يا بلح زغلول", "سيد درويش", "أغنية شعبية"),
                FamousExample("أنت عمري (مقاطع)", "أم كلثوم", "طرب")
            )
        ),
        Maqam(
            id = "shawq_afza",
            name = "مقام شوق أفزا",
            englishName = "Maqam Shawq Afza",
            family = MaqamFamily.AJAM,
            hasQuarterTone = false,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("مشتاق", "بديع", "درامي"),
            summary = "عجم نقي في القرار يتحول بشكل درامي إلى جنس الحجاز على الدرجة الخامسة.",
            detailedExplanation = "يعني اسمه «مُضاعِف الشوق». يفاجئ السامع بالانتقال من وضوح العجم المشرق إلى لوعة الحجاز (صول، لا بيمول، سي، دو).",
            sayrExplanation = "يبرز إشراق العجم في الأساس ثم يظهر الحجاز في الجواب ليخلق حواراً بين الفرح والشوق.",
            ascendingScale = listOf(C4, D4, E4, F4, G4, A_FLAT_4, B4, C5),
            descendingScale = listOf(C5, B4, A_FLAT_4, G4, F4, E4, D4, C4),
            melodyTitle = "حوار الشوق في شوق أفزا",
            melodyPhrase = listOf(
                C4.withDuration(1f), D4.withDuration(0.5f), E4.withDuration(1f),
                F4.withDuration(0.5f), G4.withDuration(1f), A_FLAT_4.withDuration(0.5f),
                B4.withDuration(1f), C5.withDuration(1.5f), B4.withDuration(0.5f),
                A_FLAT_4.withDuration(0.5f), G4.withDuration(1f), F4.withDuration(0.5f),
                E4.withDuration(0.5f), D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس العجم", "دو", "4 - 4 - 2", "1 - 1 - ½", listOf(C4, D4, E4, F4, G4), "بداية متفائلة ومفتوحة."),
            branchJins = JinsInfo("جنس الحجاز على النوى", "صول", "2 - 6 - 2", "½ - 1½ - ½", listOf(G4, A_FLAT_4, B4, C5), "تحول مشحون بالعاطفة."),
            famousExamples = listOf(
                FamousExample("يا ليل ما أطولك", "محمد عبد الوهاب", "أغنية"),
                FamousExample("الليل يطول (أجزاء)", "رياض السنباطي", "طرب")
            )
        ),

        // ==================== 7. SABA FAMILY ====================
        Maqam(
            id = "saba",
            name = "مقام الصبا",
            englishName = "Maqam Saba",
            family = MaqamFamily.SABA,
            hasQuarterTone = true,
            tonicName = "ري (دوكاه)",
            ghammazName = "فا (جهاركاه)",
            jamType = "جمع متداخل",
            moodTags = listOf("حزين", "روحاني", "شجي", "مؤثر"),
            summary = "أكثر المقامات حزناً وتأثيراً في الروح؛ يتميز بالرابعة الناقصة (ري إلى صول بيمول) ولا يكتمل بجواب الدو بل يذهب للري بيمول.",
            detailedExplanation = "مقام الصبا فريد في تركيبته غير المتماثلة؛ فدرجته الرابعة مخفوضة إلى صول بيمول، بينما جواب المقام يقف عند ري بيمول (شهناز) بدلاً من ري طبيعي، مما يولد إحساساً عميقاً بالانكسار والخشوع.",
            sayrExplanation = "ينحصر الجنس الأول حول الري والمي نصف بيمول والفا والصول بيمول، ثم ينطلق من الفا (الغمّاز) بجنس الحجاز (فا، صول بيمول، لا، سي بيمول) أو العجم على السي بيمول.",
            ascendingScale = listOf(D4, E_HALF_FLAT_4, F4, G_FLAT_4, A4, B_FLAT_4, C5, D_FLAT_5),
            descendingScale = listOf(D_FLAT_5, C5, B_FLAT_4, A4, G_FLAT_4, F4, E_HALF_FLAT_4, D4),
            melodyTitle = "تنهيدة الصبا والرابعة الناقصة",
            melodyPhrase = listOf(
                D4.withDuration(1f), E_HALF_FLAT_4.withDuration(0.5f), F4.withDuration(1f),
                G_FLAT_4.withDuration(1.5f), F4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(0.5f),
                F4.withDuration(0.5f), G_FLAT_4.withDuration(0.5f), A4.withDuration(1f),
                B_FLAT_4.withDuration(0.5f), C5.withDuration(0.5f), D_FLAT_5.withDuration(1.5f),
                B_FLAT_4.withDuration(0.5f), A4.withDuration(0.5f), G_FLAT_4.withDuration(1f),
                F4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(0.5f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الصبا", "ري (دوكاه)", "3 - 3 - 2", "¾ - ¾ - ½", listOf(D4, E_HALF_FLAT_4, F4, G_FLAT_4), "جنس رباعي بمسافة رابعة ناقصة فريدة."),
            branchJins = JinsInfo("جنس الحجاز على الجهاركاه", "فا (جهاركاه)", "2 - 6 - 2", "½ - 1½ - ½", listOf(F4, G_FLAT_4, A4, B_FLAT_4), "يتداخل مع جنس الصبا عند درجة الفا."),
            famousExamples = listOf(
                FamousExample("هو صحيح الهوى غلاب", "أم كلثوم", "طرب"),
                FamousExample("موعود (مقدمة)", "عبد الحليم حافظ", "أغنية"),
                FamousExample("التلاوة القرآنية بمقام الصبا", "تراث التلاوة", "روحاني")
            )
        ),
        Maqam(
            id = "saba_zamzam",
            name = "مقام صبا زمزم",
            englishName = "Maqam Saba Zamzam",
            family = MaqamFamily.SABA,
            hasQuarterTone = false,
            tonicName = "ري (دوكاه)",
            ghammazName = "فا (جهاركاه)",
            jamType = "جمع متداخل",
            moodTags = listOf("حزين", "درامي", "حديث"),
            summary = "نسخة خالية من أرباع التون من مقام الصبا؛ يستبدل المي نصف بيمول بالمي بيمول.",
            detailedExplanation = "يحتفظ صبا زمزم بنفس الجو المأساوي والرابعة الناقصة لمقام الصبا، غير أن خلوّه من الربع تون يجعله قابلاً للتوزيع الأوركسترالي والآلات ذات التردد الثابت كالبيانو والجيتار.",
            sayrExplanation = "سيره مطابق للصبا مع التأكيد على المي بيمول الواضحة والصول بيمول والري بيمول في القمة.",
            ascendingScale = listOf(D4, E_FLAT_4, F4, G_FLAT_4, A4, B_FLAT_4, C5, D_FLAT_5),
            descendingScale = listOf(D_FLAT_5, C5, B_FLAT_4, A4, G_FLAT_4, F4, E_FLAT_4, D4),
            melodyTitle = "عبارة صبا زمزم الدرامية",
            melodyPhrase = listOf(
                D4.withDuration(1f), E_FLAT_4.withDuration(0.5f), F4.withDuration(1f),
                G_FLAT_4.withDuration(1.5f), A4.withDuration(0.5f), B_FLAT_4.withDuration(0.5f),
                C5.withDuration(0.5f), D_FLAT_5.withDuration(1f), C5.withDuration(0.5f),
                B_FLAT_4.withDuration(0.5f), A4.withDuration(1f), G_FLAT_4.withDuration(0.5f),
                F4.withDuration(0.5f), E_FLAT_4.withDuration(0.5f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس صبا زمزم", "ري (دوكاه)", "2 - 4 - 2", "½ - 1 - ½", listOf(D4, E_FLAT_4, F4, G_FLAT_4), "جنس صبا معدّل بدون ربع تون."),
            branchJins = JinsInfo("جنس الحجاز على الجهاركاه", "فا (جهاركاه)", "2 - 6 - 2", "½ - 1½ - ½", listOf(F4, G_FLAT_4, A4, B_FLAT_4), "يتكامل مع الفا والصول بيمول."),
            famousExamples = listOf(
                FamousExample("أنت فين والحب فين (أجزاء)", "أم كلثوم", "طرب"),
                FamousExample("قارئة الفنجان (مقطع)", "عبد الحليم حافظ", "قصيدة")
            )
        ),

        // ==================== 8. SIKAH FAMILY ====================
        Maqam(
            id = "sikah",
            name = "مقام السيكاه",
            englishName = "Maqam Sikah",
            family = MaqamFamily.SIKAH,
            hasQuarterTone = true,
            tonicName = "مي نصف بيمول (سيكاه)",
            ghammazName = "صول (نوى)",
            jamType = "جمع متداخل",
            moodTags = listOf("حالم", "وقور", "تراثي", "مبهج"),
            summary = "مقام يرتكز قراره مباشرة على نغمة ربع تون (مي نصف بيمول)، ويمنح إحساساً روحياً راقياً.",
            detailedExplanation = "استمدت هذه العائلة اسمها من درجة «السيكاه» (مي نصف بيمول) التي يرتكز عليها المقام. جنسه الأساسي ثلاثي فقط (مي نصف بيمول، فا، صول) يليه جنس الراست على الصول.",
            sayrExplanation = "يبرز الاستقرار على المي نصف بيمول بالهبوط إليها من الفا والصول، ثم يواصل الصعود في جنس الراست على الصول.",
            ascendingScale = listOf(E_HALF_FLAT_4, F4, G4, A4, B_HALF_FLAT_4, C5, D5, E_HALF_FLAT_5),
            descendingScale = listOf(E_HALF_FLAT_5, D5, C5, B_HALF_FLAT_4, A4, G4, F4, E_HALF_FLAT_4),
            melodyTitle = "تحليق السيكاه على المي نصف بيمول",
            melodyPhrase = listOf(
                E_HALF_FLAT_4.withDuration(1.5f), F4.withDuration(0.5f), G4.withDuration(1f),
                A4.withDuration(0.5f), B_HALF_FLAT_4.withDuration(1f), C5.withDuration(1f),
                B_HALF_FLAT_4.withDuration(0.5f), A4.withDuration(0.5f), G4.withDuration(1f),
                F4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس السيكاه", "مي نصف بيمول (سيكاه)", "3 - 4", "¾ - 1", listOf(E_HALF_FLAT_4, F4, G4), "جنس ثلاثي رشيق يرتكز على ربع تون."),
            branchJins = JinsInfo("جنس الراست على النوى", "صول (نوى)", "4 - 3 - 3", "1 - ¾ - ¾", listOf(G4, A4, B_HALF_FLAT_4, C5), "يواصل الصعود بتوازن الراست."),
            famousExamples = listOf(
                FamousExample("يا صائغاً للغزل", "تراث", "موشح"),
                FamousExample("يا مسافر وحدك", "محمد عبد الوهاب", "أغنية")
            )
        ),
        Maqam(
            id = "huzam",
            name = "مقام الهزام",
            englishName = "Maqam Huzam",
            family = MaqamFamily.SIKAH,
            hasQuarterTone = true,
            tonicName = "مي نصف بيمول (سيكاه)",
            ghammazName = "صول (نوى)",
            jamType = "جمع متداخل",
            moodTags = listOf("طربي", "مشتاق", "ساحر", "ملوكي"),
            summary = "أشهر مقامات عائلة السيكاه؛ يجمع جنس السيكاه على المي نصف بيمول مع جنس الحجاز على الصول.",
            detailedExplanation = "يعتبر الكثيرون مقام الهزام تاج المقامات السيكاهية؛ فهو يجمع بين حلاوة قرار السيكاه وسحر الحجاز في الغمّاز (صول، لا بيمول، سي، دو)، ما يجعله حاضراً في أعظم الموشحات والأغاني.",
            sayrExplanation = "يستقر في السيكاه ثم يتألق في حجاز الصول، وقد يختم أحياناً بجنس الراست على الصول.",
            ascendingScale = listOf(E_HALF_FLAT_4, F4, G4, A_FLAT_4, B4, C5, D5, E_HALF_FLAT_5),
            descendingScale = listOf(E_HALF_FLAT_5, D5, C5, B4, A_FLAT_4, G4, F4, E_HALF_FLAT_4),
            melodyTitle = "سلطنة الهزام الطربية",
            melodyPhrase = listOf(
                E_HALF_FLAT_4.withDuration(1f), F4.withDuration(0.5f), G4.withDuration(1.5f),
                A_FLAT_4.withDuration(0.5f), B4.withDuration(1f), C5.withDuration(1.5f),
                B4.withDuration(0.5f), A_FLAT_4.withDuration(0.5f), G4.withDuration(1f),
                F4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس السيكاه", "مي نصف بيمول (سيكاه)", "3 - 4", "¾ - 1", listOf(E_HALF_FLAT_4, F4, G4), "بداية حالمة على السيكاه."),
            branchJins = JinsInfo("جنس الحجاز على النوى", "صول (نوى)", "2 - 6 - 2", "½ - 1½ - ½", listOf(G4, A_FLAT_4, B4, C5), "ينفجر بالعاطفة والسلطنة."),
            famousExamples = listOf(
                FamousExample("يا شادي الألحان (أجزاء)", "تراث", "موشح"),
                FamousExample("فات الميعاد", "أم كلثوم", "طرب"),
                FamousExample("سيرة الحب (مقطع)", "أم كلثوم", "طرب")
            )
        ),
        Maqam(
            id = "rahat_al_arwah",
            name = "مقام راحة الأرواح",
            englishName = "Maqam Rahat al-Arwah",
            family = MaqamFamily.SIKAH,
            hasQuarterTone = true,
            tonicName = "سي نصف بيمول قرار (عراق)",
            ghammazName = "ري (دوكاه)",
            jamType = "جمع متداخل",
            moodTags = listOf("هادئ", "روحي", "عتيق"),
            summary = "تصوير مقام الهزام في طبقة منخفضة على درجة العراق (سي نصف بيمول قرار).",
            detailedExplanation = "اسمه يصف أثره تماماً؛ إذ يرتكز على قرار دافئ وعميق عند السي نصف بيمول، ويجمع السيكاه في الأساس مع الحجاز على الدوكاه (ري).",
            sayrExplanation = "يتحرك بوقار في منطقة القرار حول السي نصف بيمول والدو والري، ثم يصعد لحجاز الدوكاه.",
            ascendingScale = listOf(B_HALF_FLAT_3, C4, D4, E_FLAT_4, F_SHARP_4, G4, A4, B_HALF_FLAT_4),
            descendingScale = listOf(B_HALF_FLAT_4, A4, G4, F_SHARP_4, E_FLAT_4, D4, C4, B_HALF_FLAT_3),
            melodyTitle = "سكينة راحة الأرواح",
            melodyPhrase = listOf(
                B_HALF_FLAT_3.withDuration(1.5f), C4.withDuration(0.5f), D4.withDuration(1.5f),
                E_FLAT_4.withDuration(0.5f), F_SHARP_4.withDuration(1f), G4.withDuration(1f),
                F_SHARP_4.withDuration(0.5f), E_FLAT_4.withDuration(0.5f), D4.withDuration(1f),
                C4.withDuration(0.5f), B_HALF_FLAT_3.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس السيكاه على العراق", "سي نصف بيمول قرار", "3 - 4", "¾ - 1", listOf(B_HALF_FLAT_3, C4, D4), "طبقة قرار مريحة وعميقة."),
            branchJins = JinsInfo("جنس الحجاز على الدوكاه", "ري (دوكاه)", "2 - 6 - 2", "½ - 1½ - ½", listOf(D4, E_FLAT_4, F_SHARP_4, G4), "حجاز عذب فوق القرار."),
            famousExamples = listOf(
                FamousExample("يا من حوى ورد الرياض", "صباح فخري", "موشح"),
                FamousExample("لما بدا يتثنى (تحويلة)", "تراث", "موشح")
            )
        ),
        Maqam(
            id = "iraq",
            name = "مقام العراق",
            englishName = "Maqam Iraq",
            family = MaqamFamily.SIKAH,
            hasQuarterTone = true,
            tonicName = "سي نصف بيمول قرار (عراق)",
            ghammazName = "ري (دوكاه)",
            jamType = "جمع متداخل",
            moodTags = listOf("تاريخي", "فلسفي", "تأملي"),
            summary = "تصوير لمقام السيكاه على درجة العراق، يمزج السيكاه مع البياتي على الدوكاه.",
            detailedExplanation = "مقام قديم جداً سمّيت الدرجة الموسيقية باسمه (سي نصف بيمول قرار = عراق). يمنح إحساساً تأملياً وقوراً بفضل الجمع بين السيكاه والبياتي.",
            sayrExplanation = "ينطلق من درجة العراق صاعداً إلى الدوكاه ليعزف جنس البياتي أو الراست قبل العودة إلى قراره المنخفض.",
            ascendingScale = listOf(B_HALF_FLAT_3, C4, D4, E_HALF_FLAT_4, F4, G4, A4, B_HALF_FLAT_4),
            descendingScale = listOf(B_HALF_FLAT_4, A4, G4, F4, E_HALF_FLAT_4, D4, C4, B_HALF_FLAT_3),
            melodyTitle = "لحن تاريخي في مقام العراق",
            melodyPhrase = listOf(
                B_HALF_FLAT_3.withDuration(1.5f), C4.withDuration(0.5f), D4.withDuration(1f),
                E_HALF_FLAT_4.withDuration(0.5f), F4.withDuration(0.5f), G4.withDuration(1.5f),
                F4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(0.5f), D4.withDuration(1f),
                C4.withDuration(0.5f), B_HALF_FLAT_3.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس السيكاه على العراق", "سي نصف بيمول قرار", "3 - 4", "¾ - 1", listOf(B_HALF_FLAT_3, C4, D4), "الجنس الثلاثي المؤسس."),
            branchJins = JinsInfo("جنس البياتي على الدوكاه", "ري (دوكاه)", "3 - 3 - 4", "¾ - ¾ - 1", listOf(D4, E_HALF_FLAT_4, F4, G4), "دفء البياتي فوق السيكاه."),
            famousExamples = listOf(
                FamousExample("يا ليل مالك آخر", "تراث", "بشرف وسماعي"),
                FamousExample("سماعي عراق", "إبراهيم العريان", "سماعي آلي")
            )
        ),

        // ==================== 9. NIKRIZ FAMILY ====================
        Maqam(
            id = "nikriz",
            name = "مقام النكريز",
            englishName = "Maqam Nikriz",
            family = MaqamFamily.NIKRIZ,
            hasQuarterTone = false,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("غامض", "نشيط", "ساحر", "مسرحي"),
            summary = "يرتكز على جنس النكريز الخماسي ذي الفا دييز والمي بيمول، ويكتمل بجنس النهاوند على الصول.",
            detailedExplanation = "مقام النكريز خالٍ من أرباع التون لكنه يحمل شخصية شرقية مسرحية آسرة بسبب الفجوة اللونية بين المي بيمول والفا دييز في جنس الأصل، ويختلف عن النوا أثر بوجود سي بيمول ولا طبيعية في الجواب.",
            sayrExplanation = "يركّز على الصعود والهبوط عبر المي بيمول والفا دييز، ثم يتجوّل برشاقة في جنس النهاوند على الصول.",
            ascendingScale = listOf(C4, D4, E_FLAT_4, F_SHARP_4, G4, A4, B_FLAT_4, C5),
            descendingScale = listOf(C5, B_FLAT_4, A4, G4, F_SHARP_4, E_FLAT_4, D4, C4),
            melodyTitle = "لحن نكريز الساحر بين الدو والصول",
            melodyPhrase = listOf(
                C4.withDuration(1f), D4.withDuration(0.5f), E_FLAT_4.withDuration(0.5f),
                F_SHARP_4.withDuration(1f), G4.withDuration(1.5f), A4.withDuration(0.5f),
                B_FLAT_4.withDuration(0.5f), C5.withDuration(1f), B_FLAT_4.withDuration(0.5f),
                A4.withDuration(0.5f), G4.withDuration(1f), F_SHARP_4.withDuration(0.5f),
                E_FLAT_4.withDuration(0.5f), D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس النكريز", "دو (راست)", "4 - 2 - 6 - 2", "1 - ½ - 1½ - ½", listOf(C4, D4, E_FLAT_4, F_SHARP_4, G4), "علامته الفارقة في المي بيمول والفا دييز."),
            branchJins = JinsInfo("جنس النهاوند على النوى", "صول (نوى)", "4 - 2 - 4", "1 - ½ - 1", listOf(G4, A4, B_FLAT_4, C5), "يمنح توازناً ناعماً في النهاية."),
            famousExamples = listOf(
                FamousExample("يا ليل دانا", "تراث", "قدود وموشحات"),
                FamousExample("سماعي نكريز", "تاتيوس أفندي", "سماعي شهير")
            )
        ),
        Maqam(
            id = "athar_kurd",
            name = "مقام أثر كرد",
            englishName = "Maqam Athar Kurd",
            family = MaqamFamily.NIKRIZ,
            hasQuarterTone = false,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("تأملي", "نادر", "فلسفي", "مؤثر"),
            summary = "مقام مركب حديث نسبياً يمزج بين الكرد والفا دييز (نكريز معدل) والحجاز على الصول.",
            detailedExplanation = "أثر كرد مقام فريد خالٍ من الربع تون، يبدأ بنغمة ري بيمول ثم مي بيمول ثم فا دييز فصول، ما يعطيه مزيجاً مدهشاً من رقة الكرد وغموض النكريز.",
            sayrExplanation = "يؤكد على التدرج ري بيمول - مي بيمول - فا دييز في الجنس الأول، ثم ينفتح نحو حجاز الصول.",
            ascendingScale = listOf(C4, D_FLAT_4, E_FLAT_4, F_SHARP_4, G4, A_FLAT_4, B4, C5),
            descendingScale = listOf(C5, B4, A_FLAT_4, G4, F_SHARP_4, E_FLAT_4, D_FLAT_4, C4),
            melodyTitle = "تأملات في مقام أثر كرد",
            melodyPhrase = listOf(
                C4.withDuration(1f), D_FLAT_4.withDuration(0.5f), E_FLAT_4.withDuration(0.5f),
                F_SHARP_4.withDuration(1f), G4.withDuration(1.5f), A_FLAT_4.withDuration(0.5f),
                B4.withDuration(1f), C5.withDuration(1f), B4.withDuration(0.5f),
                A_FLAT_4.withDuration(0.5f), G4.withDuration(1f), F_SHARP_4.withDuration(0.5f),
                E_FLAT_4.withDuration(0.5f), D_FLAT_4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس أثر كرد", "دو", "2 - 2 - 6 - 2", "½ - ½ - 1½ - ½", listOf(C4, D_FLAT_4, E_FLAT_4, F_SHARP_4, G4), "بناء فريد يجمع الكرد بالنكريز."),
            branchJins = JinsInfo("جنس الحجاز على النوى", "صول", "2 - 6 - 2", "½ - 1½ - ½", listOf(G4, A_FLAT_4, B4, C5), "تأكيد حجازي في القمة."),
            famousExamples = listOf(
                FamousExample("يا مسافر للجفا", "فريد الأطرش", "أغنية"),
                FamousExample("سماعي أثر كرد", "جميل عويس", "سماعي")
            )
        )
    )
}
