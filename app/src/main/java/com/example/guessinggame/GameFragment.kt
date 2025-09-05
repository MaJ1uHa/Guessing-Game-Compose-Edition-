package com.example.guessinggame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

class GameFragment : Fragment() {
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        viewModel.gameOver.observe(viewLifecycleOwner, Observer {
            if (it) {
                val action = GameFragmentDirections.actionGameFragmentToResultFragment(viewModel.wonLostMessage())
                findNavController().navigate(action)
            }
        })
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme{
                    Surface {
                        GameFragmentContent(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun GameFragmentContent(viewModel: GameViewModel){
    val guess = remember { mutableStateOf("")}
    Column (modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            SecretWordDisplayText(viewModel)
        }
        LivesLeftText(viewModel)
        IncorrectGuessesText(viewModel)
        EnterGuess(guess.value) { guess.value = it }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GuessButton {
                viewModel.makeGuess(guess.value.uppercase())
                guess.value = ""
            }
            FinishGameButton { viewModel.finishGame() }
        }
    }
}

@Composable
fun FinishGameButton(clicked:()->Unit){
    Button(onClick = clicked) {
        Text("Finish game")
    }
}

@Composable
fun EnterGuess(guess: String, changed: (String) -> Unit) {
    TextField(
        value = guess,
        onValueChange = changed,
        label = { Text("Guess a letter") }
    )
}

@Composable
fun GuessButton(clicked:()->Unit){
    Button(onClick = clicked) { Text("Guess") }
}

@Composable
fun IncorrectGuessesText(viewModel: GameViewModel){
    val incorrectGuesses = viewModel.incorrectGuesses.observeAsState()
    incorrectGuesses.value?.let {
        Text(stringResource(R.string.incorrect_guesses,it))
    }
}

@Composable
fun LivesLeftText(viewModel: GameViewModel){
    val livesLeft = viewModel.livesLeft.observeAsState()
    livesLeft.value?.let {
        Text(stringResource(R.string.lives_left,it))
    }
}

@Composable
fun SecretWordDisplayText(viewModel: GameViewModel){
    val secretWord = viewModel.secretWordDisplay.observeAsState()
    secretWord.value?.let {
        Text(text = it, fontSize = (36.sp), letterSpacing = (0.1.em))
    }
}

