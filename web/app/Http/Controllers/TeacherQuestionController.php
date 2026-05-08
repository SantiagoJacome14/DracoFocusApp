<?php

namespace App\Http\Controllers;

use App\Models\Lesson;
use App\Models\Question;
use Illuminate\Http\Request;

class TeacherQuestionController extends Controller
{
    public function index()
    {
        $questions = Question::with('lesson')->where('created_by', auth()->id())->latest()->paginate(10);
        return view('teacher.questions.index', compact('questions'));
    }

    public function create()
    {
        $lessons = Lesson::orderBy('order')->get();
        return view('teacher.questions.create', compact('lessons'));
    }

    public function store(Request $request)
    {
        $this->validateQuestion($request);

        $options = null;
        if ($request->question_type === 'multiple_choice') {
            $options = $request->options;
        }

        Question::create([
            'lesson_id' => $request->lesson_id,
            'difficulty' => $request->difficulty,
            'lesson_type' => $request->lesson_type,
            'question_type' => $request->question_type,
            'question_text' => $request->question_text,
            'options' => $options,
            'correct_answer' => $request->correct_answer,
            'created_by' => auth()->id(),
        ]);

        return redirect()->route('teacher.questions.index')->with('success', 'Pregunta creada correctamente.');
    }

    public function edit(Question $question)
    {
        if ($question->created_by !== auth()->id()) {
            abort(403);
        }
        
        $lessons = Lesson::orderBy('order')->get();
        return view('teacher.questions.edit', compact('question', 'lessons'));
    }

    public function update(Request $request, Question $question)
    {
        if ($question->created_by !== auth()->id()) {
            abort(403);
        }

        $this->validateQuestion($request);

        $options = null;
        if ($request->question_type === 'multiple_choice') {
            $options = $request->options;
        }

        $question->update([
            'lesson_id' => $request->lesson_id,
            'difficulty' => $request->difficulty,
            'lesson_type' => $request->lesson_type,
            'question_type' => $request->question_type,
            'question_text' => $request->question_text,
            'options' => $options,
            'correct_answer' => $request->correct_answer,
        ]);

        return redirect()->route('teacher.questions.index')->with('success', 'Pregunta actualizada correctamente.');
    }

    public function destroy(Question $question)
    {
        if ($question->created_by !== auth()->id()) {
            abort(403);
        }

        $question->delete();
        return redirect()->route('teacher.questions.index')->with('success', 'Pregunta eliminada correctamente.');
    }

    private function validateQuestion(Request $request)
    {
        $rules = [
            'lesson_id' => 'nullable|exists:lessons,id',
            'difficulty' => 'required|in:easy,intermediate,hard',
            'lesson_type' => 'required|in:individual,group',
            'question_type' => 'required|in:multiple_choice,text',
            'question_text' => 'required|string',
        ];

        if ($request->question_type === 'multiple_choice') {
            $rules['options'] = 'required|array|size:4';
            $rules['options.A'] = 'required|string';
            $rules['options.B'] = 'required|string';
            $rules['options.C'] = 'required|string';
            $rules['options.D'] = 'required|string';
            $rules['correct_answer'] = 'required|in:A,B,C,D';
        } else {
            $rules['correct_answer'] = 'required|string';
        }

        $request->validate($rules);
    }
}
