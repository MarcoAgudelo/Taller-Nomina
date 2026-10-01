package com.example.nominaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nominaapp.ui.theme.NominaAppTheme
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NominaAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    PantallaNomina(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaNomina(
    modifier: Modifier = Modifier
) {

    var salarioTexto by rememberSaveable {
        mutableStateOf("")
    }

    var horasDiurnasTexto by rememberSaveable {
        mutableStateOf("")
    }

    var horasNocturnasTexto by rememberSaveable {
        mutableStateOf("")
    }

    var esDominical by rememberSaveable {
        mutableStateOf(false)
    }

    var transporteEmpresa by rememberSaveable {
        mutableStateOf(false)
    }

    var resultado by rememberSaveable {
        mutableStateOf<ResultadoNomina?>(null)
    }

    var mensajeError by rememberSaveable {
        mutableStateOf("")
    }

    var errorSalario by rememberSaveable {
        mutableStateOf(false)
    }

    var errorHorasDiurnas by rememberSaveable {
        mutableStateOf(false)
    }

    var errorHorasNocturnas by rememberSaveable {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current

    val focoHorasDiurnas = remember { FocusRequester() }
    val focoHorasNocturnas = remember { FocusRequester() }

    fun calcular() {

        errorSalario = false
        errorHorasDiurnas = false
        errorHorasNocturnas = false
        mensajeError = ""

        val salario = salarioTexto.toDoubleOrNull()

        if (salario == null) {
            mensajeError = "Ingrese un salario válido"
            errorSalario = true
            resultado = null
            return
        }

        if (salario < SMMLV_2026) {
            mensajeError =
                "El salario no puede ser inferior al mínimo ($ 1.750.905)"
            errorSalario = true
            resultado = null
            return
        }

        val horasDiurnas =
            if (horasDiurnasTexto.isBlank()) {
                0.0
            } else {
                horasDiurnasTexto.toDoubleOrNull()
            }

        if (horasDiurnas == null || horasDiurnas < 0) {
            mensajeError =
                "Las horas extra deben ser un número mayor o igual a cero"
            errorHorasDiurnas = true
            resultado = null
            return
        }

        val horasNocturnas =
            if (horasNocturnasTexto.isBlank()) {
                0.0
            } else {
                horasNocturnasTexto.toDoubleOrNull()
            }

        if (horasNocturnas == null || horasNocturnas < 0) {
            mensajeError =
                "Las horas extra deben ser un número mayor o igual a cero"
            errorHorasNocturnas = true
            resultado = null
            return
        }

        if (horasDiurnas + horasNocturnas > 48) {
            mensajeError =
                "El total de horas extra no puede superar 48 en el mes"

            errorHorasDiurnas = true
            errorHorasNocturnas = true
            resultado = null
            return
        }

        resultado = calcularNomina(
            salarioBasico = salario,
            horasDiurnas = horasDiurnas,
            horasNocturnas = horasNocturnas,
            esDominical = esDominical,
            transporteEmpresa = transporteEmpresa
        )

        focusManager.clearFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Calculadora de Nómina",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Marco Antonio Agudelo Corrales",
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Código: 55291"
        )

        HorizontalDivider()

        CampoNumerico(
            etiqueta = R.string.salario_basico,
            valor = salarioTexto,
            onValueChange = {
                salarioTexto = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focoHorasDiurnas.requestFocus()
                }
            ),
            isError = errorSalario
        )

        CampoNumerico(
            etiqueta = R.string.horas_diurnas,
            valor = horasDiurnasTexto,
            onValueChange = {
                horasDiurnasTexto = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focoHorasNocturnas.requestFocus()
                }
            ),
            isError = errorHorasDiurnas,
            modifier = Modifier.focusRequester(
                focoHorasDiurnas
            )
        )

        CampoNumerico(
            etiqueta = R.string.horas_nocturnas,
            valor = horasNocturnasTexto,
            onValueChange = {
                horasNocturnasTexto = it
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            isError = errorHorasNocturnas,
            modifier = Modifier.focusRequester(
                focoHorasNocturnas
            )
        )

        FilaInterruptor(
            texto = R.string.domingo_festivo,
            checked = esDominical,
            onCheckedChange = {
                esDominical = it

                if (resultado != null) {
                    calcular()
                }
            }
        )

        FilaInterruptor(
            texto = R.string.transporte_empresa,
            checked = transporteEmpresa,
            onCheckedChange = {
                transporteEmpresa = it

                if (resultado != null) {
                    calcular()
                }
            }
        )

        if (mensajeError.isNotBlank()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    calcular()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(
                        R.string.calcular
                    )
                )
            }

            OutlinedButton(
                onClick = {
                    salarioTexto = ""
                    horasDiurnasTexto = ""
                    horasNocturnasTexto = ""

                    esDominical = false
                    transporteEmpresa = false

                    resultado = null
                    mensajeError = ""

                    errorSalario = false
                    errorHorasDiurnas = false
                    errorHorasNocturnas = false

                    focusManager.clearFocus()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(
                        R.string.limpiar
                    )
                )
            }
        }

        resultado?.let { nomina ->

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            HorizontalDivider()

            Text(
                text = stringResource(
                    R.string.devengado
                ),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.valor_hora
                ),
                valor = nomina.valorHora
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.salario_basico_resultado
                ),
                valor = nomina.salarioBasico
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.horas_extra
                ),
                valor = nomina.totalHorasExtra
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.auxilio_transporte
                ),
                valor = nomina.auxilioTransporte
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.total_devengado
                ),
                valor = nomina.totalDevengado,
                negrita = true
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = stringResource(
                    R.string.deducciones
                ),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.salud
                ),
                valor = nomina.aporteSalud
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.pension
                ),
                valor = nomina.aportePension
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.fondo_solidaridad
                ),
                valor = nomina.fondoSolidaridad
            )

            FilaResultado(
                titulo = stringResource(
                    R.string.total_deducciones
                ),
                valor = nomina.totalDeducciones,
                negrita = true
            )

            HorizontalDivider()

            Text(
                text = stringResource(
                    R.string.salario_neto
                ),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = formatearMoneda(
                    nomina.salarioNeto
                ),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            MostrarRango(
                rango = nomina.rango
            )

            Text(
                text = "Equivale a ${
                    String.format(
                        Locale.US,
                        "%.2f",
                        nomina.salarioNeto / SMMLV_2026
                    )
                } SMMLV"
            )
        }
    }
}

@Composable
fun CampoNumerico(
    @StringRes etiqueta: Int,
    valor: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    isError: Boolean,
    modifier: Modifier = Modifier
) {

    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(etiqueta)
            )
        },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        singleLine = true
    )
}

@Composable
fun FilaInterruptor(
    @StringRes texto: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = stringResource(texto),
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun FilaResultado(
    titulo: String,
    valor: Double,
    negrita: Boolean = false
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = titulo,
            fontWeight =
                if (negrita) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )

        Text(
            text = formatearMoneda(valor),
            fontWeight =
                if (negrita) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }
}

@Composable
fun MostrarRango(
    rango: RangoSalarial
) {

    val imagen: Painter

    val texto: String

    val descripcion: String

    when (rango) {

        RangoSalarial.RANGO_1 -> {
            imagen = painterResource(
                R.drawable.rango_1
            )

            texto = stringResource(
                R.string.texto_rango_1
            )

            descripcion = stringResource(
                R.string.descripcion_rango_1
            )
        }

        RangoSalarial.RANGO_2 -> {
            imagen = painterResource(
                R.drawable.rango_2
            )

            texto = stringResource(
                R.string.texto_rango_2
            )

            descripcion = stringResource(
                R.string.descripcion_rango_2
            )
        }

        RangoSalarial.RANGO_3 -> {
            imagen = painterResource(
                R.drawable.rango_3
            )

            texto = stringResource(
                R.string.texto_rango_3
            )

            descripcion = stringResource(
                R.string.descripcion_rango_3
            )
        }
    }

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    Image(
        painter = imagen,
        contentDescription = descripcion,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    )

    Text(
        text = texto,
        fontWeight = FontWeight.Bold
    )
}

fun formatearMoneda(
    valor: Double
): String {

    val formato =
        NumberFormat.getCurrencyInstance(
            Locale("es", "CO")
        )

    formato.maximumFractionDigits = 0
    formato.minimumFractionDigits = 0

    return formato.format(valor)
}

@Preview(
    showBackground = true
)
@Composable
fun PantallaNominaPreview() {

    NominaAppTheme {
        PantallaNomina()
    }
}