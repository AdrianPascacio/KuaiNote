package com.example.kuai_notes_project;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

public class Stable_Content_Generator_For_Test {

    public DB_Tasks DB_T ;
    public DB_Notes DB_N ;
    private String seed_text_midium = "Una mañana, tras un sueño intranquilo, Gregorio Samsa se despertó convertido en un monstruoso insecto. Estaba echado de espaldas sobre un duro caparazón y, al alzar la cabeza, vio su vientre convexo y oscuro, surcado por curvadas callosidades, sobre el que casi no se aguantaba la colcha, que estaba a punto de escurrirse hasta el suelo. Numerosas patas, penosamente delgadas en comparación con el grosor normal de sus piernas, se agitaban sin concierto. —¿Qué me ha ocurrido? No estaba soñando. Su habitación, una habitación normal, aunque muy pequeña, tenía el aspecto habitual. Sobre la mesa había desparramado un muestrario de paños —Samsa era viajante de comercio—, y de la pared colgaba una estampa recientemente recortada de una revista ilustrada y puesta en un marco dorado. La estampa mostraba a una mujer tocada con un gorro de pieles, envuelta en una estola también de pieles, y que, muy erguida, esgrimía un amplio manguito, asimismo de piel, que ocultaba todo su antebrazo. Gregorio miró hacia la ventana; estaba nublado, y sobre el cinc del alféizar repiqueteaban las gotas de lluvia, lo que le hizo sentir una gran melancolía. «Bueno —pensó—; ¿y si siguiese durmiendo un rato y me olvidase de todas estas locuras?» Pero no era posible, pues Gregorio tenía la costumbre de dormir sobre el lado derecho, y su actual estado no le permitía adoptar tal postura. Por más que se esforzara volvía a quedar de espaldas. Intentó en vano esta operación numerosas veces; cerró los ojos para no tener que ver aquella confusa agitación de patas, que no cesó hasta que notó en el costado un dolor leve y punzante, un dolor jamás sentido hasta entonces. —¡Qué cansada es la profesión que he elegido! —se dijo—. Siempre de viaje. Las preocupaciones son mucho mayores cuando se trabaja fuera, por no hablar de las molestias propias de los viajes: estar pendiente de los enlaces de los trenes; la comida mala, irregular; relaciones que cambian constantemente, que nunca llegan a ser verdaderamente cordiales, y en las que no tienen cabida los sentimientos. ¡Al diablo con todo! Sintió en el vientre una ligera picazón. Lentamente, se estiró sobre la espalda en dirección a la cabecera de la cama, para poder alzar mejor la cabeza. Vio que el sitio que le picaba estaba cubierto de extraños puntitos blancos. Intentó rascarse con una pata; pero tuvo que retirarla inmediatamente, pues el roce le producía escalofríos. —Estoy atontado de tanto madrugar —se dijo—. No duermo lo suficiente. Hay viajantes que viven mucho mejor. Cuando a media mañana regreso a la fonda para anotar los pedidos, me los encuentro desayunando cómodamente sentados. Si yo, con el jefe que tengo, hiciese lo mismo, me despedirían en el acto. Lo cual, probablemente sería lo mejor que me podría pasar. Si no fuese por mis padres, ya hace tiempo que me hubiese marchado. Hubiera ido a ver el director y le habría dicho todo lo que pienso. Se caería de la mesa, ésa sobre la que se sienta para, desde aquella altura, hablar a los empleados, que, como es sordo, han de acercársele mucho. Pero todavía no he perdido la esperanza. En cuanto haya reunido la cantidad necesaria para pagarle la deuda de mis padres —unos cinco o seis años todavía—, me va a oír. Bueno; pero, por ahora, lo que tengo que hacer es levantarme, que el tren sale a las cinco. Eran más de las seis y media, y las manecillas seguían avanzando tranquilamente. En realidad, ya eran casi las siete menos cuarto. ¿Es que no había sonado el despertador? Desde la cama se veía que estaba puesto a las cuatro; por tanto, tenía que haber sonado. Pero ¿era posible seguir durmiendo a pesar de aquel sonido que hacía estremecer hasta los muebles? Su sueño no había sido tranquilo. Pero, por eso mismo, debía de haber dormido al final más profundamente. ¿Qué podía hacer ahora? El tren siguiente salía a las siete; para cogerlo tendría que darse muchísima prisa. El muestrario no estaba aún empaquetado, y él mismo no se sentía nada dispuesto. Además, aunque alcanzase el tren, no evitaría reprimenda del amo, pues el mozo del almacén, que había acudido al tren a las cinco,";
    private String seed_text_short = "Una mañana, tras un sueño intranquilo, Gregorio Samsa se despertó convertido en un monstruoso insecto. Estaba echado de espaldas sobre un duro caparazón y, al alzar la cabeza, vio su vientre convexo y oscuro, surcado por curvadas callosidades, sobre el que casi no se aguantaba la colcha, que estaba a punto de escurrirse hasta el suelo. Numerosas patas, penosamente delgadas en comparación con el grosor normal de sus piernas, se agitaban sin concierto. —¿Qué me ha ocurrido? No estaba soñando. Su habitación, una habitación normal, aunque muy pequeña, tenía el aspecto habitual. Sobre la mesa había desparramado un muestrario de paños —Samsa era viajante de comercio—, y de la pared colgaba una estampa recientemente recortada de una revista ilustrada y puesta en un marco dorado. La estampa mostraba a una mujer tocada con un gorro de pieles, envuelta en una estola también de pieles, y que, muy erguida, esgrimía un amplio manguito, asimismo de piel, que ocultaba todo su antebrazo. Gregorio miró hacia la ventana; estaba.";

    public void Stable_Tasks_Generator(Context context, int Content_Number, int SubTasks_Number, int task_start, int task_length){
        DB_T = new DB_Tasks(context);

        for(int i = Content_Number ; i>=0; i--){
            long _current_time = System.currentTimeMillis();
            boolean stable_boolean = (i & 1) == 1; ///Bitwise i & 1 → comprobacion del ultimo bit (par o impar)
            //Fill_Tasks(i, stable_boolean,task_start, task_end, _current_time);
            String _title = i + seed_text_short.substring(task_start,task_length);

            long new_task_id = DB_T.Insert_Task_L_for_test_random_generator(_current_time, _title, _title, stable_boolean, 0L, 0, 0,stable_boolean,stable_boolean);
            if( stable_boolean == true){//if has sub tasks
                for(int j = 0; j <= SubTasks_Number;j++){
                    boolean stable_boolean_sub =  ((j >> 1) & 1) == 1; ///Bitwise (j >> 1) & 1 → comprobacion del segundo bit  (1010 >> 1) == 0101 → 010[1] & 1 compara el utlimo bit (que antes era el segundo)(par o impar)
                    DB_T.Insert_Task_Sub_L(new_task_id,j+_title,stable_boolean_sub,j);
                }
            }
        }
    }
    public void Stable_Note_Generator(Context context, int Content_Number, int title_start, int title_length, int note_start, int note_length){
        DB_N = new DB_Notes(context);

        for(int i = Content_Number ; i>=0; i--){
            boolean stable_boolean = (i & 1) == 1; ///Bitwise i & 1 → comprobacion del ultimo bit (par o impar)
            long _current_time = System.currentTimeMillis();

            String _title = i + seed_text_short.substring(title_start, title_start + title_length );
            String _note = seed_text_short.substring(note_start, note_start + note_length);

            DB_N.Insert_Note_L(_current_time, _title, _note, stable_boolean, 0L, 0, 0);
        }
    }
}