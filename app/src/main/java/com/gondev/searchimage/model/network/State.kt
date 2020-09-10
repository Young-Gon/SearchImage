package com.gondev.searchimage.model.network

import java.io.PrintWriter
import java.io.StringWriter

sealed class State {
	object Loading : State()

	object Success : State()

	class Error(val throwable: Throwable) : State(){
		fun getStackTrace(): String {
			val stringWriter = StringWriter()
			throwable.printStackTrace(PrintWriter(stringWriter))
			return stringWriter.toString()
		}
	}

	companion object{
		fun loading() = Loading

		fun success() = Success

		fun error(throwable: Throwable) = Error(throwable)
	}
}
