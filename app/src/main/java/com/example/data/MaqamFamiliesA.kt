package com.example.data

import com.example.data.NoteCatalog.A3
import com.example.data.NoteCatalog.A4
import com.example.data.NoteCatalog.A_FLAT_4
import com.example.data.NoteCatalog.A_HALF_FLAT_4
import com.example.data.NoteCatalog.B3
import com.example.data.NoteCatalog.B4
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

internal object MaqamFamiliesA {
    val rastAndBayatiAndHijaz: List<Maqam> = listOf(
        // ==================== 1. RAST FAMILY ====================
        Maqam(
            id = "rast",
            name = "مقام الراست",
            englishName = "Maqam Rast",
            family = MaqamFamily.RAST,
            hasQuarterTone = true,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("فخم", "مبهج", "متزن", "أصيل"),
            summary = "أمّ المقامات العربية وأعرقها؛ يجمع بين الوقار والبهجة مع استخدام السيكاه في الدرجتين الثالثة والسابعة.",
            detailedExplanation = "يُعدّ مقام الراست الركيزة الأساسية للموسيقى العربية المشرقية، فاسمه يعني «المستقيم» بالفارسية. يتميز بوجود ربع تون في الدرجة الثالثة (مي نصف بيمول) والسابعة صعوداً (سي نصف بيمول)، بينما تُخفض السابعة إلى سي بيمول عند الهبوط نحو القرار.",
            sayrExplanation = "يبدأ العازف باستعراض جنس الراست حول درجة الدو ثم يصعد إلى الغمّاز (صول) ليبرز جنس الراست أو النهوند عليه، ويهبط مستبدلاً السي نصف بيمول بسي بيمول قبل الاستقرار على الدو.",
            ascendingScale = listOf(C4, D4, E_HALF_FLAT_4, F4, G4, A4, B_HALF_FLAT_4, C5),
            descendingScale = listOf(C5, B_FLAT_4, A4, G4, F4, E_HALF_FLAT_4, D4, C4),
            melodyTitle = "جملة استهلالية واستقرار على الراست",
            melodyPhrase = listOf(
                C4.withDuration(1f), D4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(0.5f),
                F4.withDuration(1f), G4.withDuration(1.5f), A4.withDuration(0.5f),
                B_HALF_FLAT_4.withDuration(0.5f), C5.withDuration(1f), B_FLAT_4.withDuration(0.5f),
                A4.withDuration(0.5f), G4.withDuration(1f), F4.withDuration(0.5f),
                E_HALF_FLAT_4.withDuration(1f), D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo(
                name = "جنس الراست",
                rootNoteArabic = "دو (راست)",
                arabicIntervals = "4 - 3 - 3",
                westernIntervals = "1 - ¾ - ¾",
                notes = listOf(C4, D4, E_HALF_FLAT_4, F4, G4),
                description = "جنس خماسي يمتد من الدو إلى الصول ويمنح المقام شخصيته المتزنة."
            ),
            branchJins = JinsInfo(
                name = "جنس راست على النوى",
                rootNoteArabic = "صول (نوى)",
                arabicIntervals = "4 - 3 - 3",
                westernIntervals = "1 - ¾ - ¾",
                notes = listOf(G4, A4, B_HALF_FLAT_4, C5),
                description = "يُعزف صعوداً بسي نصف بيمول، وتتحول إلى سي بيمول (نهوند على الصول) عند الهبوط."
            ),
            famousExamples = listOf(
                FamousExample("يا شادي الألحان", "سيد درويش", "موشح كلاسيكي"),
                FamousExample("يا مال الشام", "صباح فخري", "قدود حلبية"),
                FamousExample("أروح لمين", "أم كلثوم", "طرب كلاسيكي")
            )
        ),
        Maqam(
            id = "suznak",
            name = "مقام السوزناك",
            englishName = "Maqam Suznak",
            family = MaqamFamily.RAST,
            hasQuarterTone = true,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("رومانسي", "مشتعل", "طربي"),
            summary = "تزاوج ساحر يجمع رصانة جنس الراست في الأساس مع شغف جنس الحجاز في الفرع.",
            detailedExplanation = "يعني اسمه «المحرق أو الملهب للعواطف». ينطلق من أرضية الراست المتماسكة، ثم يفاجئ المستمع بجنس الحجاز على درجة الصول، مما يخلق تبايناً عاطفياً آسراً.",
            sayrExplanation = "يستعرض اللحن جنس الراست بين الدو والصول، ثم يقفز إلى لا بيمول وسي طبيعي لإظهار نكهة الحجاز الساحرة قبل الرجوع إلى الدو.",
            ascendingScale = listOf(C4, D4, E_HALF_FLAT_4, F4, G4, A_FLAT_4, B4, C5),
            descendingScale = listOf(C5, B4, A_FLAT_4, G4, F4, E_HALF_FLAT_4, D4, C4),
            melodyTitle = "تحويلة السوزناك من الراست إلى الحجاز",
            melodyPhrase = listOf(
                C4.withDuration(1f), D4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(1f),
                F4.withDuration(0.5f), G4.withDuration(1f), A_FLAT_4.withDuration(0.5f),
                B4.withDuration(1f), C5.withDuration(1.5f), B4.withDuration(0.5f),
                A_FLAT_4.withDuration(0.5f), G4.withDuration(1f), E_HALF_FLAT_4.withDuration(0.5f),
                D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الراست", "دو (راست)", "4 - 3 - 3", "1 - ¾ - ¾", listOf(C4, D4, E_HALF_FLAT_4, F4, G4), "أساس متوازن يمهّد للتحويل."),
            branchJins = JinsInfo("جنس الحجاز على النوى", "صول (نوى)", "2 - 6 - 2", "½ - 1½ - ½", listOf(G4, A_FLAT_4, B4, C5), "جنس رباعي يعطي التوهج الشرقي."),
            famousExamples = listOf(
                FamousExample("يا غصن نقا", "تراث أندلسي", "موشح"),
                FamousExample("فكروني (مقطع)", "أم كلثوم", "طرب")
            )
        ),
        Maqam(
            id = "nairuz",
            name = "مقام النيروز",
            englishName = "Maqam Nairuz",
            family = MaqamFamily.RAST,
            hasQuarterTone = true,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("مشرق", "عذب", "فلكلوري"),
            summary = "يبدأ بجنس الراست على الدو ويتفرع إلى جنس البياتي على الصول.",
            detailedExplanation = "مقام النيروز (ويُسمى أيضاً يَكاه إذا صُوّر على الصول قرار) يتميز بعذوبة فائقة لأنه يدمج ثبات الراست مع حنان البياتي في الجواب.",
            sayrExplanation = "يمهد في جنس الراست حتى الصول، ثم يتحول إلى البياتي (صول، لا نصف بيمول، سي بيمول، دو) ليمنح إحساساً دافئاً قبل العودة للدو.",
            ascendingScale = listOf(C4, D4, E_HALF_FLAT_4, F4, G4, A_HALF_FLAT_4, B_FLAT_4, C5),
            descendingScale = listOf(C5, B_FLAT_4, A_HALF_FLAT_4, G4, F4, E_HALF_FLAT_4, D4, C4),
            melodyTitle = "جملة نيروزية بعبق البياتي",
            melodyPhrase = listOf(
                C4.withDuration(1f), E_HALF_FLAT_4.withDuration(1f), F4.withDuration(0.5f),
                G4.withDuration(1f), A_HALF_FLAT_4.withDuration(0.5f), B_FLAT_4.withDuration(0.5f),
                C5.withDuration(1f), B_FLAT_4.withDuration(0.5f), A_HALF_FLAT_4.withDuration(0.5f),
                G4.withDuration(1f), F4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(1f),
                D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الراست", "دو", "4 - 3 - 3", "1 - ¾ - ¾", listOf(C4, D4, E_HALF_FLAT_4, F4, G4), "الأساس الراستي المتزن."),
            branchJins = JinsInfo("جنس البياتي على النوى", "صول", "3 - 3 - 4", "¾ - ¾ - 1", listOf(G4, A_HALF_FLAT_4, B_FLAT_4, C5), "جنس بياتي ناعم على الدرجة الخامسة."),
            famousExamples = listOf(
                FamousExample("يا ليل الصب", "الحصري القيرواني / تراث", "موشح"),
                FamousExample("على بلد المحبوب", "أم كلثوم", "طرب")
            )
        ),
        Maqam(
            id = "mahur",
            name = "مقام الماهور",
            englishName = "Maqam Mahur",
            family = MaqamFamily.RAST,
            hasQuarterTone = true,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("نشيط", "عالي", "ملكي"),
            summary = "راست أصلي في الأساس مع جنس عجم واضح على درجة الصول والسي الطبيعية.",
            detailedExplanation = "يشبه الماهور مقام الراست في نصفه السفلي، لكنه يعتمد في نصفه العلوي على السي الطبيعية (ماهور) دائماً صعوداً وهبوطاً، مما يمنحه بريقاً احتفالياً أقرب إلى العجم.",
            sayrExplanation = "غالباً ما يبدأ الماهور من جواب الدو أو المنطقة العليا لاستعراض جنس العجم على الصول قبل الهبوط نحو قرار الراست.",
            ascendingScale = listOf(C4, D4, E_HALF_FLAT_4, F4, G4, A4, B4, C5),
            descendingScale = listOf(C5, B4, A4, G4, F4, E_HALF_FLAT_4, D4, C4),
            melodyTitle = "إشراقة الماهور من الجواب إلى القرار",
            melodyPhrase = listOf(
                C5.withDuration(1f), B4.withDuration(0.5f), A4.withDuration(0.5f),
                G4.withDuration(1f), A4.withDuration(0.5f), B4.withDuration(0.5f),
                C5.withDuration(1.5f), G4.withDuration(0.5f), F4.withDuration(0.5f),
                E_HALF_FLAT_4.withDuration(1f), D4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الراست", "دو", "4 - 3 - 3", "1 - ¾ - ¾", listOf(C4, D4, E_HALF_FLAT_4, F4, G4), "أساس الراست على القرار."),
            branchJins = JinsInfo("جنس العجم على النوى", "صول", "4 - 4 - 2", "1 - 1 - ½", listOf(G4, A4, B4, C5), "يعطي إشراقاً ووضوحاً عالياً في الجواب."),
            famousExamples = listOf(
                FamousExample("يا شقيق الروح", "تراث حلبي", "موشح"),
                FamousExample("سماعي ماهور", "جميل عويس", "سماعي آلي")
            )
        ),

        // ==================== 2. BAYATI FAMILY ====================
        Maqam(
            id = "bayati",
            name = "مقام البياتي",
            englishName = "Maqam Bayati",
            family = MaqamFamily.BAYATI,
            hasQuarterTone = true,
            tonicName = "ري (دوكاه)",
            ghammazName = "صول (نوى)",
            jamType = "جمع متصل",
            moodTags = listOf("دافئ", "شجي", "شعبي", "عذب"),
            summary = "أكثر المقامات انتشاراً في العالم العربي؛ يرتكز على الري مع ربع تون في الدرجة الثانية (مي نصف بيمول).",
            detailedExplanation = "يمثل مقام البياتي نبض الوجدان الشعبي والديني العربي. تمنح مسافته الأولى (ثلاثة أرباع التون بين الري والمي نصف بيمول) دفئاً إنسانياً رقيقاً لا هو بالحزين القاتم ولا بالصاخب.",
            sayrExplanation = "يتركز في جنس البياتي حول الري والصول، ثم يبرز جنس النهاوند على الصول (صول، لا، سي بيمول، دو)، وقد يلمس السي نصف بيمول صعوداً.",
            ascendingScale = listOf(D4, E_HALF_FLAT_4, F4, G4, A4, B_FLAT_4, C5, D5),
            descendingScale = listOf(D5, C5, B_FLAT_4, A4, G4, F4, E_HALF_FLAT_4, D4),
            melodyTitle = "تقسيمة بياتي كلاسيكية على الدوكاه",
            melodyPhrase = listOf(
                D4.withDuration(1f), E_HALF_FLAT_4.withDuration(0.5f), F4.withDuration(0.5f),
                G4.withDuration(1.5f), F4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(0.5f),
                D4.withDuration(0.5f), G4.withDuration(1f), A4.withDuration(0.5f),
                B_FLAT_4.withDuration(0.5f), C5.withDuration(1f), B_FLAT_4.withDuration(0.5f),
                A4.withDuration(0.5f), G4.withDuration(1f), F4.withDuration(0.5f),
                E_HALF_FLAT_4.withDuration(0.5f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس البياتي", "ري (دوكاه)", "3 - 3 - 4", "¾ - ¾ - 1", listOf(D4, E_HALF_FLAT_4, F4, G4), "قلب المقام بدفئه المعهود."),
            branchJins = JinsInfo("جنس النهاوند على النوى", "صول (نوى)", "4 - 2 - 4", "1 - ½ - 1", listOf(G4, A4, B_FLAT_4, C5, D5), "يمتد من الغمّاز صول حتى جواب الري."),
            famousExamples = listOf(
                FamousExample("يا ليل يا عيني", "تراث عربي", "تواشيح ومواويل"),
                FamousExample("ست الحبايب", "محمد عبد الوهاب", "أغنية عربية"),
                FamousExample("يا صلاة الزين", "زكريا أحمد", "طقطوقة")
            )
        ),
        Maqam(
            id = "hussaini",
            name = "مقام الحسيني",
            englishName = "Maqam Hussaini",
            family = MaqamFamily.BAYATI,
            hasQuarterTone = true,
            tonicName = "ري (دوكاه)",
            ghammazName = "لا (حسيني)",
            jamType = "جمع متداخل",
            moodTags = listOf("روحاني", "مؤثر", "تراثي"),
            summary = "شقيق البياتي، لكنه يركّز على درجة الحسيني (لا) كغمّاز رئيسي ويستخدم السي نصف بيمول.",
            detailedExplanation = "يتطابق الحسيني مع البياتي في قراره، غير أن مساره اللحني يميل بقوة نحو الدرجة الخامسة (لا / حسيني) ويعتمد جنس البياتي على اللا أو جنس الراست على الصول مع سي نصف بيمول.",
            sayrExplanation = "ينطلق غالباً من درجة اللا (حسيني) متجولاً في الأعلى بسي نصف بيمول، ثم يهبط تدريجياً ليستقر على الدوكاه (ري).",
            ascendingScale = listOf(D4, E_HALF_FLAT_4, F4, G4, A4, B_HALF_FLAT_4, C5, D5),
            descendingScale = listOf(D5, C5, B_FLAT_4, A4, G4, F4, E_HALF_FLAT_4, D4),
            melodyTitle = "صعود الحسيني نحو الأوج والرجوع",
            melodyPhrase = listOf(
                A4.withDuration(1.5f), B_HALF_FLAT_4.withDuration(0.5f), C5.withDuration(0.5f),
                D5.withDuration(1f), C5.withDuration(0.5f), B_HALF_FLAT_4.withDuration(0.5f),
                A4.withDuration(1f), G4.withDuration(0.5f), F4.withDuration(0.5f),
                E_HALF_FLAT_4.withDuration(1f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس البياتي", "ري (دوكاه)", "3 - 3 - 4", "¾ - ¾ - 1", listOf(D4, E_HALF_FLAT_4, F4, G4), "نفس أساس البياتي على الدوكاه."),
            branchJins = JinsInfo("جنس البياتي على الحسيني", "لا (حسيني)", "3 - 3 - 4", "¾ - ¾ - 1", listOf(A4, B_HALF_FLAT_4, C5, D5), "يمنح الحسيني نبرة الأنين والرفعة."),
            famousExamples = listOf(
                FamousExample("يا ديرتي", "سيد درويش", "دور"),
                FamousExample("يا غريب الدار", "تراث خليجي وعراقي", "مقام")
            )
        ),
        Maqam(
            id = "bayati_shuri",
            name = "مقام البياتي شوري",
            englishName = "Maqam Bayati Shuri",
            family = MaqamFamily.BAYATI,
            hasQuarterTone = true,
            tonicName = "ري (دوكاه)",
            ghammazName = "صول (نوى)",
            jamType = "جمع متصل",
            moodTags = listOf("مؤثر", "شجي", "تأملي"),
            summary = "يجمع بين دفء البياتي على الري ولهيب جنس الحجاز على الصول.",
            detailedExplanation = "يُسمى أيضاً «قارجيغار». يُعد من أجمل المقامات المركبة؛ حيث يبدأ بسلاسة البياتي ثم يفتح أفقاً درامياً بإحلال لا بيمول وسي طبيعي في النصف الأعلى.",
            sayrExplanation = "يتدرج من الري إلى الصول في جنس البياتي، ثم يظهر لون الحجاز واضحاً بين الصول والري العالي قبل الهبوط إلى الدوكاه.",
            ascendingScale = listOf(D4, E_HALF_FLAT_4, F4, G4, A_FLAT_4, B4, C5, D5),
            descendingScale = listOf(D5, C5, B4, A_FLAT_4, G4, F4, E_HALF_FLAT_4, D4),
            melodyTitle = "انتقال البياتي شوري إلى جنس الحجاز",
            melodyPhrase = listOf(
                D4.withDuration(1f), E_HALF_FLAT_4.withDuration(0.5f), F4.withDuration(0.5f),
                G4.withDuration(1f), A_FLAT_4.withDuration(0.5f), B4.withDuration(1f),
                C5.withDuration(0.5f), D5.withDuration(1f), B4.withDuration(0.5f),
                A_FLAT_4.withDuration(0.5f), G4.withDuration(1f), F4.withDuration(0.5f),
                E_HALF_FLAT_4.withDuration(0.5f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس البياتي", "ري (دوكاه)", "3 - 3 - 4", "¾ - ¾ - 1", listOf(D4, E_HALF_FLAT_4, F4, G4), "بداية حانية وناعمة."),
            branchJins = JinsInfo("جنس الحجاز على النوى", "صول (نوى)", "2 - 6 - 2", "½ - 1½ - ½", listOf(G4, A_FLAT_4, B4, C5, D5), "تباين قوي يمنح المقام شجناً مضاعفاً."),
            famousExamples = listOf(
                FamousExample("يا ليل طل (أجزاء)", "تراث", "طرب"),
                FamousExample("كل ده كان ليه (مقطع)", "محمد عبد الوهاب", "أغنية")
            )
        ),
        Maqam(
            id = "muhayyar",
            name = "مقام المحيّر",
            englishName = "Maqam Muhayyar",
            family = MaqamFamily.BAYATI,
            hasQuarterTone = true,
            tonicName = "ري (دوكاه)",
            ghammazName = "ري جواب (محيّر)",
            jamType = "جمع متصل",
            moodTags = listOf("مرتفع", "درامي", "ساحر"),
            summary = "ينطلق من جواب الري (المحيّر) هابطاً عبر الحسيني والراست حتى يستقر على الدوكاه.",
            detailedExplanation = "يشترك في درجاته مع البياتي والحسيني، لكن شخصيته تكمن في الهبوط من القمة (درجة المحيّر D5) بتركيز خاص على جنس الراست على الصول.",
            sayrExplanation = "يفتتح العازف في الطبقة العالية عند جواب الري، ويدور في جنس البياتي العالي، ثم يهبط بسلاسة إلى قرار الري.",
            ascendingScale = listOf(D4, E_HALF_FLAT_4, F4, G4, A4, B_HALF_FLAT_4, C5, D5),
            descendingScale = listOf(D5, C5, B_FLAT_4, A4, G4, F4, E_HALF_FLAT_4, D4),
            melodyTitle = "نزول المحيّر من القمة إلى القرار",
            melodyPhrase = listOf(
                D5.withDuration(1.5f), C5.withDuration(0.5f), B_HALF_FLAT_4.withDuration(0.5f),
                A4.withDuration(1f), G4.withDuration(0.5f), A4.withDuration(0.5f),
                B_FLAT_4.withDuration(0.5f), A4.withDuration(0.5f), G4.withDuration(1f),
                F4.withDuration(0.5f), E_HALF_FLAT_4.withDuration(0.5f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس البياتي", "ري (دوكاه)", "3 - 3 - 4", "¾ - ¾ - 1", listOf(D4, E_HALF_FLAT_4, F4, G4), "الركوز النهائي على الدوكاه."),
            branchJins = JinsInfo("جنس الراست على النوى", "صول (نوى)", "4 - 3 - 3", "1 - ¾ - ¾", listOf(G4, A4, B_HALF_FLAT_4, C5, D5), "جنس الصعود في الطبقة العليا."),
            famousExamples = listOf(
                FamousExample("يا بهجة الروح", "تراث أندلسي", "موشح"),
                FamousExample("قل للمليحة في الخمار الأسود", "صباح فخري", "قصيدة")
            )
        ),

        // ==================== 3. HIJAZ FAMILY ====================
        Maqam(
            id = "hijaz",
            name = "مقام الحجاز",
            englishName = "Maqam Hijaz",
            family = MaqamFamily.HIJAZ,
            hasQuarterTone = false,
            tonicName = "ري (دوكاه)",
            ghammazName = "صول (نوى)",
            jamType = "جمع متصل",
            moodTags = listOf("روحاني", "صحراوي", "حزين", "عميق"),
            summary = "أشهر المقامات الشرقية عالمياً؛ يمتاز بالقفزة التعبيرية بين المي بيمول والفا دييز (بعد ونصف).",
            detailedExplanation = "مقام الحجاز خالٍ غالباً من أرباع التون (أو قد تُلمس في الزخارف)، وسرّه في جنس الحجاز الرباعي (ري، مي بيمول، فا دييز، صول) الذي يحمل مسافة ثانية زائدة تعبّر عن الوجد والخشوع والرحابة.",
            sayrExplanation = "ينطلق من الري ويبرز التوتر العاطفي بين المي بيمول والفا دييز، ثم ينتقل للغمّاز صول ليعزف جنس النهاوند أو الراست قبل العودة للاستقرار على الري.",
            ascendingScale = listOf(D4, E_FLAT_4, F_SHARP_4, G4, A4, B_FLAT_4, C5, D5),
            descendingScale = listOf(D5, C5, B_FLAT_4, A4, G4, F_SHARP_4, E_FLAT_4, D4),
            melodyTitle = "نداء الحجاز الأصيل",
            melodyPhrase = listOf(
                D4.withDuration(1f), E_FLAT_4.withDuration(0.5f), F_SHARP_4.withDuration(1f),
                G4.withDuration(1.5f), A4.withDuration(0.5f), B_FLAT_4.withDuration(0.5f),
                A4.withDuration(0.5f), G4.withDuration(1f), F_SHARP_4.withDuration(1f),
                E_FLAT_4.withDuration(0.5f), D4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الحجاز", "ري (دوكاه)", "2 - 6 - 2", "½ - 1½ - ½", listOf(D4, E_FLAT_4, F_SHARP_4, G4), "المسافة الأيقونية للموسيقى الشرقية."),
            branchJins = JinsInfo("جنس النهاوند على النوى", "صول (نوى)", "4 - 2 - 4", "1 - ½ - 1", listOf(G4, A4, B_FLAT_4, C5, D5), "يخفف من حدة الحجاز برومانسية دافئة."),
            famousExamples = listOf(
                FamousExample("فوق النخل", "ناظم الغزالي", "تراث عراقي"),
                FamousExample("يا مسهرني (مقاطع)", "أم كلثوم", "طرب"),
                FamousExample("أذان مكة والمدينة", "تراث إسلامي", "ابتهال وأذان")
            )
        ),
        Maqam(
            id = "hijaz_kar",
            name = "مقام حجاز كار",
            englishName = "Maqam Hijaz Kar",
            family = MaqamFamily.HIJAZ,
            hasQuarterTone = false,
            tonicName = "دو (راست)",
            ghammazName = "صول (نوى)",
            jamType = "جمع منفصل",
            moodTags = listOf("فخم", "درامي", "ساحر", "ملكي"),
            summary = "مقام متناظر الجنسين؛ يجمع جنس الحجاز على الدو وجنس الحجاز على الصول.",
            detailedExplanation = "يرتكز على الدو ويستخدم جنسين متطابقين من الحجاز، مما ينتج سلماً مزدوج التوهج (دو، ري بيمول، مي، فا، صول، لا بيمول، سي، دو) خالياً من أرباع التون لكنه شديد الشرقية.",
            sayrExplanation = "يميل في بدايته للنزول قليلاً تحت الدو لإظهار السي الطبيعية، ثم يتنقل بين حجاز الدو وحجاز الصول ببراعة احتفالية.",
            ascendingScale = listOf(C4, D_FLAT_4, E4, F4, G4, A_FLAT_4, B4, C5),
            descendingScale = listOf(C5, B4, A_FLAT_4, G4, F4, E4, D_FLAT_4, C4),
            melodyTitle = "تألق حجاز كار المتناظر",
            melodyPhrase = listOf(
                C4.withDuration(1f), B3.withDuration(0.5f), C4.withDuration(0.5f),
                D_FLAT_4.withDuration(0.5f), E4.withDuration(1f), F4.withDuration(0.5f),
                G4.withDuration(1f), A_FLAT_4.withDuration(0.5f), B4.withDuration(1f),
                C5.withDuration(1.5f), B4.withDuration(0.5f), A_FLAT_4.withDuration(0.5f),
                G4.withDuration(1f), E4.withDuration(0.5f), D_FLAT_4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الحجاز", "دو (راست)", "2 - 6 - 2", "½ - 1½ - ½", listOf(C4, D_FLAT_4, E4, F4), "حجاز على درجة الدو."),
            branchJins = JinsInfo("جنس الحجاز على النوى", "صول (نوى)", "2 - 6 - 2", "½ - 1½ - ½", listOf(G4, A_FLAT_4, B4, C5), "تناظر تام يضاعف الدراما."),
            famousExamples = listOf(
                FamousExample("لما بدا يتثنى (أجزاء)", "تراث", "موشح"),
                FamousExample("يا بهجة القلب", "سيد درويش", "طرب"),
                FamousExample("ليالي الحلمية (نكهة)", "ميشيل المصري", "موسيقى تصويرية")
            )
        ),
        Maqam(
            id = "shadd_araban",
            name = "مقام شت عربان",
            englishName = "Maqam Shadd Araban",
            family = MaqamFamily.HIJAZ,
            hasQuarterTone = false,
            tonicName = "صول قرار (يكاه)",
            ghammazName = "ري (دوكاه)",
            jamType = "جمع منفصل",
            moodTags = listOf("عتيق", "وقور", "تأملي"),
            summary = "تصوير لمقام حجاز كار على درجة الصول القرار (يكاه)، بطابع أعمق وأرخم.",
            detailedExplanation = "الشت عربان هو نفسه بناء حجاز كار ولكن على طبقة صوتية منخفضة تبدأ من الصول قرار (G3)، مما يمنح الآلات الوترية كالعود رنيناً غليظاً جليلاً.",
            sayrExplanation = "يركز على منطقة القرار العميقة حول الصول قرار، ثم يصعد لجنس الحجاز الثاني على الري.",
            ascendingScale = listOf(G3, NoteCatalog.A_FLAT_3, B3, C4, D4, E_FLAT_4, F_SHARP_4, G4),
            descendingScale = listOf(G4, F_SHARP_4, E_FLAT_4, D4, C4, B3, NoteCatalog.A_FLAT_3, G3),
            melodyTitle = "عزف عميق على شت عربان",
            melodyPhrase = listOf(
                G3.withDuration(1f), NoteCatalog.A_FLAT_3.withDuration(0.5f), B3.withDuration(1f),
                C4.withDuration(0.5f), D4.withDuration(1f), E_FLAT_4.withDuration(0.5f),
                F_SHARP_4.withDuration(1f), G4.withDuration(1.5f), F_SHARP_4.withDuration(0.5f),
                E_FLAT_4.withDuration(0.5f), D4.withDuration(1f), C4.withDuration(0.5f),
                B3.withDuration(0.5f), NoteCatalog.A_FLAT_3.withDuration(0.5f), G3.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الحجاز", "صول قرار (يكاه)", "2 - 6 - 2", "½ - 1½ - ½", listOf(G3, NoteCatalog.A_FLAT_3, B3, C4), "حجاز عميق في منطقة القرار."),
            branchJins = JinsInfo("جنس الحجاز على الدوكاه", "ري (دوكاه)", "2 - 6 - 2", "½ - 1½ - ½", listOf(D4, E_FLAT_4, F_SHARP_4, G4), "جنس الفرع على النغمة الخامسة."),
            famousExamples = listOf(
                FamousExample("سماعي شت عربان", "جميل بك الطنبوري", "سماعي عثماني/عربي"),
                FamousExample("موشح يا من لعبت به شمول", "تراث", "موشح")
            )
        ),
        Maqam(
            id = "zanjiran",
            name = "مقام زنجران",
            englishName = "Maqam Zanjiran",
            family = MaqamFamily.HIJAZ,
            hasQuarterTone = false,
            tonicName = "دو (راست)",
            ghammazName = "فا (جهاركاه)",
            jamType = "جمع متصل",
            moodTags = listOf("شرقي", "مشرق", "حيوي"),
            summary = "يمزج بين جنس الحجاز على الدو وجنس العجم على الفا، ليخفف التوتر بنهاية مشرقة.",
            detailedExplanation = "على عكس حجاز كار، يضع مقام زنجران جنس العجم على درجة الفا في النصف العلوي (فا، صول، لا، سي بيمول، دو)، مما يعطيه إشراقة غير متوقعة بعد بداية الحجاز.",
            sayrExplanation = "يؤسس لجنس الحجاز في البداية ثم يرتكز على الفا ليبرز رحابة العجم، ويعود في الختام للدو.",
            ascendingScale = listOf(C4, D_FLAT_4, E4, F4, G4, A4, B_FLAT_4, C5),
            descendingScale = listOf(C5, B_FLAT_4, A4, G4, F4, E4, D_FLAT_4, C4),
            melodyTitle = "تحوّل زنجران من الحجاز إلى العجم",
            melodyPhrase = listOf(
                C4.withDuration(1f), D_FLAT_4.withDuration(0.5f), E4.withDuration(1f),
                F4.withDuration(1f), G4.withDuration(0.5f), A4.withDuration(0.5f),
                B_FLAT_4.withDuration(0.5f), C5.withDuration(1.5f), B_FLAT_4.withDuration(0.5f),
                A4.withDuration(0.5f), G4.withDuration(0.5f), F4.withDuration(1f),
                E4.withDuration(0.5f), D_FLAT_4.withDuration(0.5f), C4.withDuration(2f)
            ),
            rootJins = JinsInfo("جنس الحجاز", "دو", "2 - 6 - 2", "½ - 1½ - ½", listOf(C4, D_FLAT_4, E4, F4), "بداية حجازية مركّزة."),
            branchJins = JinsInfo("جنس العجم على الجهاركاه", "فا", "4 - 4 - 2", "1 - 1 - ½", listOf(F4, G4, A4, B_FLAT_4, C5), "لون عجمي مشرق على الفا."),
            famousExamples = listOf(
                FamousExample("يا غزالاً زانه الحور", "تراث", "موشح"),
                FamousExample("حكاية غرامي (لمسة)", "فريد الأطرش", "طرب")
            )
        )
    )
}
